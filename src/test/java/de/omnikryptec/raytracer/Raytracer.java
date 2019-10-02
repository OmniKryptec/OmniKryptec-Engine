package de.omnikryptec.raytracer;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

import de.omnikryptec.core.Omnikryptec;
import de.omnikryptec.core.Scene;
import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.shader.UniformFloat;
import de.omnikryptec.libapi.exposed.render.shader.UniformVec3;
import de.omnikryptec.libapi.exposed.window.SurfaceBuffer;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.libapi.opengl.framebuffer.GLFrameBuffer;
import de.omnikryptec.libapi.opengl.shader.GLShader;
import de.omnikryptec.render.AdaptiveCamera;
import de.omnikryptec.render.Camera;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.renderer.LocalRendererContext;
import de.omnikryptec.render.renderer.Renderer;
import de.omnikryptec.util.math.MathUtil;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.settings.keys.KeysAndButtons;
import de.omnikryptec.util.updater.Time;
import de.omnikryptec.util.math.Mathf;

public class Raytracer extends Omnikryptec implements Renderer, IUpdatable {
    
    public static void main(final String[] args) {
        new Raytracer().start();
    }
    
    @Override
    protected void configure(final Settings<LoaderSetting> loadersettings, final Settings<LibSetting> libsettings,
            final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apisetting, KeySettings keys) {
        libsettings.set(LibSetting.DEBUG, false);
        windowSettings.set(WindowSetting.Name, "Raytracer");
    }
    
    @Override
    protected void onInitialized() {
        getResourceManager().load(false, false, "intern:/de/pcfreak9000/raytracer/");
        Scene s = getGame().createNewScene();
        s.getRendering().addRenderer(this);
        s.setGameLogic(this);
        camera = new AdaptiveCamera((w, h) -> new Matrix4f().perspective(Mathf.toRadians(60), w / (float) h, 1, 2));
        s.getRendering().setMainProjection(camera);
        initShader();
    }
    
    private GLFrameBuffer image;
    
    private GLShader computeShader;
    
    private Camera camera;
    private UniformFloat time;
    private UniformVec3 eye, ray00, ray01, ray10, ray11;
    
    private void initShader() {
        computeShader = (GLShader) LibAPIManager.instance().getGLFW().getRenderAPI().createShader();
        computeShader.create("raytracer");
        eye = computeShader.getUniform("eye");
        ray00 = computeShader.getUniform("ray00");
        ray01 = computeShader.getUniform("ray01");
        ray10 = computeShader.getUniform("ray10");
        ray11 = computeShader.getUniform("ray11");
        time = computeShader.getUniform("time");
    }
    
    private void loadRays(IProjection projection) {
        Matrix4fc inv = projection.getRawProjection().invert(new Matrix4f());
        Matrix4f worldspace = inv.mul(projection.getProjection(), new Matrix4f());
        Vector4f pos = worldspace.transform(new Vector4f(0, 0, 0, 1));
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
        if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_LEFT_CONTROL)) {
            dt *= 3;
        }
        float vx = 0, vy = 0, vz = 0;
        if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_W)) {
            vz = -1;
        } else if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_S)) {
            vz = 1;
        }
        if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_A)) {
            vx = -1;
        } else if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_D)) {
            vx = 1;
        }
        if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_SPACE)) {
            vy = 1;
        } else if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_LEFT_SHIFT)) {
            vy = -1;
        }
        vx *= dt;
        vy *= dt;
        vz *= dt;
        cam.getTransform().localspaceWrite().translate(vx, vy, vz);
    }
    
    @Override
    public void update(Time time) {
        camInput(camera, time.deltaf);
    }
    
    @Override
    public void init(LocalRendererContext context, FrameBuffer target) {
        image = (GLFrameBuffer) context.getRenderAPI().createFrameBuffer(target.getWidth(), target.getHeight(), 0, 1);
        image.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA32, 0));
    }
    
    @Override
    public void resizeFBOs(LocalRendererContext context, SurfaceBuffer screen) {
        image = (GLFrameBuffer) image.resizedClone(screen.getWidth(), screen.getHeight());
    }
    
    @Override
    public void render(Time time, IProjection projection, LocalRendererContext context) {
        computeShader.bindShader();
        this.time.loadFloat(time.currentf);
        loadRays(projection);
        image.bindImageTexture(0, 0, 0, false, 0, 0, FBAttachmentFormat.RGBA32);
        computeShader.dispatchCompute(MathUtil.toPowerOfTwo(image.getWidth() / 8),
                MathUtil.toPowerOfTwo(image.getHeight() / 8), 1);
        image.renderDirect(0);
    }
    
    @Override
    public void deinit(LocalRendererContext context) {
    }
    
}
