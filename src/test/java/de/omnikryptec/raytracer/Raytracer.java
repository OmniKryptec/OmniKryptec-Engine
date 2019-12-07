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
import de.omnikryptec.libapi.exposed.input.CursorType;
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
import de.omnikryptec.util.math.Mathf;
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
        windowSettings.set(WindowSetting.Name, "Raytracer");
        windowSettings.set(WindowSetting.CursorState, CursorType.DISABLED);
    }

    @Override
    protected void onInitialized() {
        getResourceManager().load(false, false, "intern:/de/pcfreak9000/raytracer/");
        final Scene s = getGame().createNewScene(true);
        s.getRendering().addRenderer(this);
        s.setGameLogic(this);
        this.camera = new AdaptiveCamera(
                (w, h) -> new Matrix4f().perspective(Mathf.toRadians(80), w / (float) h, 1, 2));
        s.getRendering().setMainProjection(this.camera);
        initShader();
    }

    private GLFrameBuffer image;

    private GLShader computeShader;

    private Camera camera;
    private UniformFloat time;
    private UniformVec3 eye, ray00, ray01, ray10, ray11;

    private void initShader() {
        this.computeShader = (GLShader) LibAPIManager.instance().getGLFW().getRenderAPI().createShader();
        this.computeShader.create("raytracer");
        this.eye = this.computeShader.getUniform("eye");
        this.ray00 = this.computeShader.getUniform("ray00");
        this.ray01 = this.computeShader.getUniform("ray01");
        this.ray10 = this.computeShader.getUniform("ray10");
        this.ray11 = this.computeShader.getUniform("ray11");
        this.time = this.computeShader.getUniform("time");
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
            dt *= 3;
        }
        float vx = 0, vy = 0, vz = 0;
        if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_W)) {
            vz = 1;
        } else if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_S)) {
            vz = -1;
        }
        if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_A)) {
            vx = 1;
        } else if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_D)) {
            vx = -1;
        }
        if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_SPACE)) {
            vy = -1;
        } else if (getInput().isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_LEFT_SHIFT)) {
            vy = 1;
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
    public void init(final LocalRendererContext context, final FrameBuffer target) {
        this.image = (GLFrameBuffer) context.getRenderAPI().createFrameBuffer(target.getWidth(), target.getHeight(), 0,
                1);
        this.image.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA32, 0));
    }

    @Override
    public void resizeFBOs(final LocalRendererContext context, final SurfaceBuffer screen) {
        this.image = (GLFrameBuffer) this.image.resizedClone(screen.getWidth(), screen.getHeight());
    }

    @Override
    public void render(final Time time, final IProjection projection, final LocalRendererContext context) {
        this.computeShader.bindShader();
        this.time.loadFloat(time.currentf);
        loadRays(this.camera);
        this.image.bindImageTexture(0, 0, 0, false, 0, 0, FBAttachmentFormat.RGBA32);
        this.computeShader.dispatchCompute(MathUtil.toPowerOfTwo(this.image.getWidth() / 8),
                MathUtil.toPowerOfTwo(this.image.getHeight() / 8), 1);
        this.image.renderDirect(0);
    }

    @Override
    public void deinit(final LocalRendererContext context) {
    }

}
