package de.omnikryptec.raytracer;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import de.omnikryptec.core.EngineLoader;
import de.omnikryptec.core.update.ILayer;
import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.input.InputManager;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.shader.UniformFloat;
import de.omnikryptec.libapi.exposed.render.shader.UniformVec3;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.libapi.opengl.OpenGLRenderAPI;
import de.omnikryptec.libapi.opengl.framebuffer.GLFrameBuffer;
import de.omnikryptec.libapi.opengl.shader.GLShader;
import de.omnikryptec.render.Camera;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.batch.SimpleBatch2D;
import de.omnikryptec.render.renderer.RendererUtil;
import de.omnikryptec.util.math.MathUtil;
import de.omnikryptec.util.math.Mathf;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.settings.keys.KeysAndButtons;
import de.omnikryptec.util.updater.Time;

public class Raytracer extends EngineLoader implements IUpdatable {
    
    public static void main(final String[] args) {
        new Raytracer().start();
    }
    
    @Override
    protected void configure(final Settings<LoaderSetting> loadersettings, final Settings<LibSetting> libsettings,
            final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apisetting) {
        libsettings.set(LibSetting.DEBUG, false);
        windowSettings.set(WindowSetting.Name, "Raytracer");
    }
    
    @Override
    protected void onInitialized() {
        getResourceManager().instantLoad(false, false, "intern:/de/pcfreak9000/raytracer/");
        mgr = new InputManager(null);
        getGameController().getGlobalScene().setUpdateableSync(this);
        renderApi = (OpenGLRenderAPI) RenderAPI.get();
        image = (GLFrameBuffer) renderApi.createFrameBuffer(1024, 768, 0, 1);
        image.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA32, 0));
        computeShader = (GLShader) renderApi.createShader();
        computeShader.create("raytracer");
        initUniforms();
        camera = new Camera(new Matrix4f().perspective((float) Math.toRadians(60),
                image.getWidth() / (float) image.getHeight(), 1, 2));
        //camera.getTransform().localspaceWrite().setLookAt(3, 2, 7, 0, 0.5f, 0, 0, 1, 0);
    }
    
    private GLFrameBuffer image;    
    private OpenGLRenderAPI renderApi;
    
    private GLShader computeShader;
    
    private Camera camera;
    private UniformFloat time;
    private UniformVec3 eye, ray00, ray01, ray10, ray11;
    
    private InputManager mgr;
    
    @Override
    public void init(ILayer layer) {
        mgr.init(layer);
        
    }
    
    private void initUniforms() {
        eye = computeShader.getUniform("eye");
        ray00 = computeShader.getUniform("ray00");
        ray01 = computeShader.getUniform("ray01");
        ray10 = computeShader.getUniform("ray10");
        ray11 = computeShader.getUniform("ray11");
        time = computeShader.getUniform("time");
    }
    
    private void loadRays(Camera projection) {
        Matrix4fc inv = projection.getRawProjection().invert(new Matrix4f());
        
        Vector4f pos = projection.getTransform().worldspace().transform(new Vector4f(0, 0, 0, 1));
        eye.loadVec3(pos.x, pos.y, pos.z);
        prepareRay(-1, -1, inv, ray00, pos);
        prepareRay(-1, 1, inv, ray01, pos);
        prepareRay(1, -1, inv, ray10, pos);
        prepareRay(1, 1, inv, ray11, pos);
    }
    
    private void prepareRay(float x, float y, Matrix4fc proj, UniformVec3 u, Vector4f pos) {
        Vector4f v = new Vector4f(x, y, 0, 1);
        proj.transform(v);
        v.mul(1 / v.w);
        v.normalize();
        u.loadVec3(new Vector3f(v.x, v.y, v.z));
    }
    
    private void camInput(Camera cam, float dt) {
        float old = dt;
        if (mgr.isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_LEFT_CONTROL)) {
            dt *= 3;
        }
        float vx = 0, vy = 0, vz = 0;
        if (mgr.isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_W)) {
            vz = -1;
        } else if (mgr.isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_S)) {
            vz = 1;
        }
        if (mgr.isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_A)) {
            vx = -1;
        } else if (mgr.isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_D)) {
            vx = 1;
        }
        if (mgr.isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_SPACE)) {
            vy = 1;
        } else if (mgr.isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_LEFT_SHIFT)) {
            vy = -1;
        }
        vx *= dt;
        vy *= dt;
        vz *= dt;
        cam.getTransform().localspaceWrite().translate(vx, vy, vz);
        
        //
        //        GLFW.glfwSetCursorPos(renderApi.getWindow().getID(), renderApi.getWindow().getWindowWidth() / 2,
        //                renderApi.getWindow().getWindowHeight() / 2);
    }
    
    @Override
    public void update(Time time) {
        mgr.update(time);
        camInput(camera, time.deltaf);
        computeShader.bindShader();
        this.time.loadFloat(time.currentf);
        loadRays(camera);
        image.bindImageTexture(0, 0, 0, false, 0, 0, FBAttachmentFormat.RGBA32);
        computeShader.dispatchCompute(MathUtil.toPowerOfTwo(image.getWidth() / 8),
                MathUtil.toPowerOfTwo(image.getHeight() / 8), 1);
        
        image.renderDirect(0);
    }
    
}
