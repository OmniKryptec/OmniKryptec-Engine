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
    
    private static final float BOX_SIZE = 1f;
    private static final int SIZE = 25;
    private static final int MAX_STEPS = 150;
    
    private static class SSBOHelper {
        private final GLShaderStorageBuffer ssbo;
        private final float[] array;
        
        public SSBOHelper(int index, int size) {
            this.ssbo = new GLShaderStorageBuffer();
            this.ssbo.setDescription(BufferUsage.Dynamic, Type.FLOAT, size, index);
            this.array = new float[size];
        }
        
        public void push() {
            FloatBuffer b = BufferUtils.createFloatBuffer(array.length);
            b.put(array);
            this.ssbo.updateData(b);
        }
        
        public float[] array() {
            return array;
        }
        
    }
    
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
        this.computeShader.bindShader();
        this.size.loadInt(SIZE);
        this.boxSize.loadFloat(BOX_SIZE);
        this.maxSteps.loadInt(MAX_STEPS);
        int SIZE_CUBED = SIZE * SIZE * SIZE;
        SSBOHelper dataHelper = new SSBOHelper(1, SIZE_CUBED);
        SSBOHelper speedHelper = new SSBOHelper(2, SIZE_CUBED);
        SSBOHelper redHelper = new SSBOHelper(3, SIZE_CUBED);
        SSBOHelper greenHelper = new SSBOHelper(4, SIZE_CUBED);
        SSBOHelper blueHelper = new SSBOHelper(5, SIZE_CUBED);
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                for (int z = 0; z < SIZE; z++) {
                    float dataV = 0;
                    float speedV = 300000;
                    Color color = new Color();
                    if (y == 0) {
                        dataV = 1;
                        color.setAll(0.2f);
                    }
                    if (y == 0 && x == SIZE / 2 && z == SIZE / 2) {
                        color.setAll(1);
                    }
                    if (y <= 8 && y != 0) {
                        speedV = 235000;
                    }
                    if (y == 8) {
                        color.set(0, 0.1f, 0.6f);
                    }
                    
                    if (y <= 9 && (x == 0 || x == SIZE - 1 || z == 0 || z == SIZE - 1)) {
                        color.set(0, 0.7f, 0.7f);
                        dataV = 1;
                    }
                    
                    dataHelper.array()[x + y * SIZE + z * SIZE * SIZE] = dataV;
                    speedHelper.array()[x + y * SIZE + z * SIZE * SIZE] = speedV;
                    redHelper.array()[x + y * SIZE + z * SIZE * SIZE] = color.getR();
                    greenHelper.array()[x + y * SIZE + z * SIZE * SIZE] = color.getG();
                    blueHelper.array()[x + y * SIZE + z * SIZE * SIZE] = color.getB();
                }
            }
        }
        dataHelper.push();
        speedHelper.push();
        redHelper.push();
        greenHelper.push();
        blueHelper.push();
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
        float vx = 0, vy = 0, vz = 0;
        if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_W)) {
            vz = 4;
        } else if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_S)) {
            vz = -4;
        }
        if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_A)) {
            vx = 4;
        } else if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_D)) {
            vx = -4;
        }
        if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_SPACE)) {
            vy = -4;
        } else if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_LEFT_SHIFT)) {
            vy = 4;
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
    }
    
    @Override
    public void update(final Time time) {
        camInput(this.camera, time.deltaf);
    }
    
    @Override
    public void init(ViewManager vm, RenderAPI api) {
        this.image = (GLFrameBuffer) api.createFrameBuffer(vm.getMainView().getTargetFbo().getWidth() * 2,
                vm.getMainView().getTargetFbo().getHeight() * 2, 0, 1);
        this.image.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA32, 0));
    }
    
    private void checkFBOs(FrameBuffer target) {
        this.image = (GLFrameBuffer) this.image.resizeAndDeleteOrThis(target.getWidth(), target.getHeight());
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
