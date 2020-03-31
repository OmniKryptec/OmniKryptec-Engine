/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.raytracer;

import java.nio.FloatBuffer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import de.omnikryptec.core.Omnikryptec;
import de.omnikryptec.core.Scene;
import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.input.CursorType;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.RenderAPI.BufferUsage;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.shader.UniformFloat;
import de.omnikryptec.libapi.exposed.render.shader.UniformInt;
import de.omnikryptec.libapi.exposed.render.shader.UniformVec3;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.libapi.opengl.buffer.GLShaderStorageBuffer;
import de.omnikryptec.libapi.opengl.framebuffer.GLFrameBuffer;
import de.omnikryptec.libapi.opengl.shader.GLShader;
import de.omnikryptec.render.AdaptiveCamera;
import de.omnikryptec.render.Camera;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.renderer.Renderer;
import de.omnikryptec.render.renderer.ViewManager;
import de.omnikryptec.render.renderer.ViewManager.EnvironmentKey;
import de.omnikryptec.util.Logger;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.MathUtil;
import de.omnikryptec.util.math.Mathf;
import de.omnikryptec.util.profiling.Profiler;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.settings.keys.KeysAndButtons;
import de.omnikryptec.util.updater.Time;

public class Raytracer extends Omnikryptec implements Renderer, IUpdatable {
    
    public static void main(final String[] args) {
        new Raytracer().start();
    }
    
    @Override
    protected void configure(final Settings<LoaderSetting> loadersettings, final Settings<LibSetting> libsettings,
            final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apisetting,
            final KeySettings keys) {
        libsettings.set(LibSetting.DEBUG, false);
        libsettings.set(LibSetting.LOGGING_MIN, Logger.LogType.Debug);
        windowSettings.set(WindowSetting.Name, "Raytracer");
        windowSettings.set(WindowSetting.CursorState, CursorType.DISABLED);
        //Profiler.setEnabled(true);
    }
    
    @Override
    protected void onInitialized() {
        getResourceManager().load(false, false, "intern:/de/pcfreak9000/raytracer/");
        final Scene s = getGame().createAndAddScene();
        s.getViewManager().addRenderer(this);
        s.setGameLogic(this);
        this.camera = new AdaptiveCamera(
                (w, h) -> new Matrix4f().perspective(Mathf.toRadians(80), w / (float) h, 1, 2));
        s.getViewManager().getMainView().setProjection(camera);
        initShader();
    }
    
    @Override
    protected void onShutdown() {
        //System.out.println(Profiler.currentInfo());
    }
    
    private GLFrameBuffer image;
    
    private GLShader computeShader;
    
    private Camera camera;
    private UniformFloat time, boxSize;
    private UniformVec3 eye, ray00, ray01, ray10, ray11;
    private UniformInt size, maxSteps;
    
    private static final float DEFAULT_BOX_SIZE = 1f;
    private static final int DEFAULT_SIZE = 25;
    private static final int DEFAULT_MAX_STEPS = 150;
    
    private static final int TOTAL_MODES = 3;
    private static final float CD_THRESHHOLD = 0.2f;
    
    private static final int MAX_SIZE = 400;
    private static final int MAXMAX_STEPS = 1000;
    private static final int MAX_SIZE_CUBED = MAX_SIZE * MAX_SIZE * MAX_SIZE;
    
    private static class SSBOHelper {
        private final GLShaderStorageBuffer ssbo;
        private final float[] array;
        
        public SSBOHelper(int index, int size) {
            this.ssbo = new GLShaderStorageBuffer();
            this.ssbo.setDescription(BufferUsage.Dynamic, Type.FLOAT, size, index);
            this.array = new float[size];
        }
        
        public void push(int actual) {
            FloatBuffer b = BufferUtils.createFloatBuffer(actual);
            b.put(array, 0, actual);
            this.ssbo.updateData(b);
        }
        
        public float[] array() {
            return array;
        }
        
    }
    
    SSBOHelper dataHelper;
    SSBOHelper speedHelper;
    SSBOHelper redHelper;
    SSBOHelper greenHelper;
    SSBOHelper blueHelper;
    private int currentMode = -1;
    private float cd = 0;
    
    private float cd1 = 0, cd2 = 0;
    
    private float currentBoxSize;
    private int currentSize;
    private int currentMaxSteps;
    private float currentResMult = 1;
    
    private void initShader() {
        this.computeShader = (GLShader) LibAPIManager.instance().getGLFW().getRenderAPI().createShader();
        this.computeShader.create("raytracer");
        this.eye = this.computeShader.getUniform("eye");
        this.ray00 = this.computeShader.getUniform("ray00");
        this.ray01 = this.computeShader.getUniform("ray01");
        this.ray10 = this.computeShader.getUniform("ray10");
        this.ray11 = this.computeShader.getUniform("ray11");
        this.time = this.computeShader.getUniform("time");
        this.boxSize = this.computeShader.getUniform("BOX_SIZE");
        this.size = this.computeShader.getUniform("SIZE");
        this.maxSteps = this.computeShader.getUniform("MAX_STEPS");
        dataHelper = new SSBOHelper(1, MAX_SIZE_CUBED);
        speedHelper = new SSBOHelper(2, MAX_SIZE_CUBED);
        redHelper = new SSBOHelper(3, MAX_SIZE_CUBED);
        greenHelper = new SSBOHelper(4, MAX_SIZE_CUBED);
        blueHelper = new SSBOHelper(5, MAX_SIZE_CUBED);
        resetSizes();
        updateMode(0);
    }
    
    private void updateSizes() {
        updateMode(currentMode);
        this.computeShader.bindShader();
        this.size.loadInt(currentSize);
        this.boxSize.loadFloat(currentBoxSize);
        this.maxSteps.loadInt(currentMaxSteps);
    }
    
    private void resetSizes() {
        this.currentSize = DEFAULT_SIZE;
        this.currentBoxSize = DEFAULT_BOX_SIZE;
        this.currentMaxSteps = DEFAULT_MAX_STEPS;
        updateSizes();
    }
    
    private void updateMode(int newMode) {
        for (int x = 0; x < currentSize; x++) {
            for (int y = 0; y < currentSize; y++) {
                for (int z = 0; z < currentSize; z++) {
                    float dataV = 0;
                    float speedV = 300000;
                    Color color = new Color();
                    if (newMode == 0) {
                        if (y == 0 || y == currentSize - 1) {
                            dataV = 1;
                            if (y == 0) {
                                color.set(0, 1, 0.5f);
                            } else {
                                color.set(0, 0, 1);
                            }
                        }
                        if (y == 0 && x == currentSize / 2 && z == currentSize / 2) {
                            color.setAll(1);
                        }
                        if (y <= 8 && y != 0) {
                            speedV = 235000;
                        }
                        if (y == 8) {
                            color.set(0, 0.1f, 0.6f);
                        }
                        
                        if (y <= 9 && (x == 0 || x == currentSize - 1 || z == 0 || z == currentSize - 1)) {
                            color.set(0, 0.7f, 0.7f);
                            dataV = 1;
                        }
                        if (x == y && x == z) {
                            color.set(1, 0, 0);
                            dataV = 1;
                        }
                    } else if (newMode == 1) {
                        if (y > currentSize / 3 && y < 2 * currentSize / 3f && x > currentSize / 3
                                && x < 2 * currentSize / 3f && z > currentSize / 3 && z < 2 * currentSize / 3f) {
                            dataV = 0;
                            speedV = 235000;
                            //color.set(0, 0.3f, 0);
                        }
                        
                        if (x == currentSize / 2 && z == currentSize / 2) {
                            dataV = 1;
                            speedV = 300000;
                            color.set(1, 0, 0);
                        }
                    } else if (newMode == 2) {
                        color.randomizeRGB();
                        speedV = (float) Math.random();
                        dataV = (float) Math.random();
                    }
                    dataHelper.array()[x + y * currentSize + z * currentSize * currentSize] = dataV;
                    speedHelper.array()[x + y * currentSize + z * currentSize * currentSize] = speedV;
                    redHelper.array()[x + y * currentSize + z * currentSize * currentSize] = color.getR();
                    greenHelper.array()[x + y * currentSize + z * currentSize * currentSize] = color.getG();
                    blueHelper.array()[x + y * currentSize + z * currentSize * currentSize] = color.getB();
                }
            }
        }
        int currSizeCubed = currentSize * currentSize * currentSize;
        dataHelper.push(currSizeCubed);
        speedHelper.push(currSizeCubed);
        redHelper.push(currSizeCubed);
        greenHelper.push(currSizeCubed);
        blueHelper.push(currSizeCubed);
        this.currentMode = newMode;
    }
    
    private void loadRays(final Camera cam) {
        final Matrix4f inv = cam.getRawProjection().invert(new Matrix4f());
        final Matrix4f wInv = cam.getTransform().worldspace().invert(new Matrix4f());
        final Vector4f pos = wInv.transform(new Vector4f(0, 0, 0, 1));
        this.eye.loadVec3(pos.x, pos.y, pos.z);
        prepareRay(-1, -1, inv, wInv, this.ray00, pos);
        prepareRay(-1, 1, inv, wInv, this.ray01, pos);
        prepareRay(1, -1, inv, wInv, this.ray10, pos);
        prepareRay(1, 1, inv, wInv, this.ray11, pos);
    }
    
    private void prepareRay(final float x, final float y, final Matrix4fc proj, final Matrix4fc stuff,
            final UniformVec3 u, final Vector4f pos) {
        final Vector4f v = new Vector4f(x, y, 0, 1);
        proj.transform(v);
        v.mul(1 / v.w);
        v.w = 0;
        stuff.transform(v);
        v.normalize();
        u.loadVec3(new Vector3f(v.x, v.y, v.z));
    }
    
    float x, y, z, ry, rx;
    
    private void camInput(final Camera cam, float dt) {
        if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_LEFT_CONTROL)) {
            dt /= 3;
        }
        int change = 1;
        if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_TAB)) {
            change = 10;
        }
        float vx = 0, vy = 0, vz = 0;
        if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_W)) {
            vz = 4 * change;
        } else if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_S)) {
            vz = -4 * change;
        }
        if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_A)) {
            vx = 4 * change;
        } else if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_D)) {
            vx = -4 * change;
        }
        if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_SPACE)) {
            vy = -4 * change;
        } else if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_LEFT_SHIFT)) {
            vy = 4 * change;
        }
        float ry = 0, rx = 0;
        ry = (float) getInput().getMousePositionDelta().x() * 0.1f;
        rx = (float) getInput().getMousePositionDelta().y() * 0.1f;
        ry *= dt;
        rx *= dt;
        vx *= dt;
        vy *= dt;
        vz *= dt;
        this.ry += ry;
        this.rx += rx;
        cam.getTransform().localspaceWrite().rotation(this.rx, 1, 0, 0);
        cam.getTransform().localspaceWrite().rotate(this.ry, 0, 1, 0);
        final Vector4f t = new Vector4f(vx, vy, vz, 0);
        cam.getTransform().worldspace().invert(new Matrix4f()).transform(t);
        this.x += t.x;
        this.y += t.y;
        this.z += t.z;
        
        cam.getTransform().localspaceWrite().translate(this.x, this.y, this.z);
        
        cd += dt;
        if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_R) && cd > CD_THRESHHOLD) {
            updateMode((currentMode + 1) % TOTAL_MODES);
            cd = 0;
        }
        if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_ENTER)) {
            resetSizes();
            this.currentResMult = 1;
        }
        cd1 += dt;
        if (cd1 > CD_THRESHHOLD) {
            if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_1)) {
                cd1 = 0;
                this.currentSize = Math.max(0, this.currentSize - change);
                updateSizes();
            } else if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_2)) {
                cd1 = 0;
                this.currentSize = Math.min(MAX_SIZE, this.currentSize + change);
                updateSizes();
            }
        }
        cd2 += dt;
        if (cd2 > CD_THRESHHOLD) {
            if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_3)) {
                cd2 = 0;
                this.currentMaxSteps = Math.max(0, this.currentMaxSteps - change);
                updateSizes();
            } else if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_4)) {
                cd2 = 0;
                this.currentMaxSteps = Math.min(MAXMAX_STEPS, this.currentMaxSteps + change);
                updateSizes();
            }
        }
        if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_7)) {
            this.currentResMult = Math.max(0, this.currentResMult - dt);
        } else if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_8)) {
            this.currentResMult = Math.min(10, this.currentResMult + dt);
        }
        
        if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_5)) {
            this.currentBoxSize = Math.max(0, this.currentBoxSize - dt * change);
            updateSizes();
        } else if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_6)) {
            this.currentBoxSize += dt * change;
            updateSizes();
        }
    }
    
    @Override
    public void update(final Time time) {
        camInput(this.camera, time.deltaf);
    }
    
    @Override
    public void init(ViewManager vm, RenderAPI api) {
        this.image = (GLFrameBuffer) api.createFrameBuffer(
                (int) (vm.getMainView().getTargetFbo().getWidth() * currentResMult),
                (int) (vm.getMainView().getTargetFbo().getHeight() * currentResMult), 0, 1);
        this.image.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA32, 0));
    }
    
    private void checkFBOs(FrameBuffer target) {
        this.image = (GLFrameBuffer) this.image.resizeAndDeleteOrThis((int) (target.getWidth() * currentResMult),
                (int) (target.getHeight() * currentResMult));
    }
    
    @Override
    public void render(ViewManager viewManager, RenderAPI api, IProjection projection, FrameBuffer target,
            Settings<EnvironmentKey> envSettings, Time time) {
        checkFBOs(target);
        this.computeShader.bindShader();
        this.time.loadFloat(time.currentf);
        loadRays(this.camera);
        this.image.bindImageTexture(0, 0, 0, false, 0, 0, FBAttachmentFormat.RGBA32);
        this.computeShader.dispatchCompute(MathUtil.toPowerOfTwo(this.image.getWidth() / 8),
                MathUtil.toPowerOfTwo(this.image.getHeight() / 8), 1);
        this.image.renderDirect(0);
    }
    
}
