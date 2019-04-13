package de.omnikryptec.raytracer;

import org.joml.Matrix3x2f;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.core.ComfortTest;
import de.omnikryptec.core.EngineLoader;
import de.omnikryptec.core.update.IUpdatable;
import de.omnikryptec.core.update.UpdateableFactory;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.TextureFormat;
import de.omnikryptec.libapi.exposed.render.RenderAPI.SurfaceBufferType;
import de.omnikryptec.libapi.exposed.window.SurfaceBuffer;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.libapi.opengl.OpenGLRenderAPI;
import de.omnikryptec.libapi.opengl.framebuffer.GLFrameBuffer;
import de.omnikryptec.libapi.opengl.shader.GLShader;
import de.omnikryptec.render.batch.ShadedBatch2D;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;


public class Raytracer extends EngineLoader implements IUpdatable {
    
    public static void main(final String[] args) {
        new Raytracer().start();
    }
    
    @Override
    protected void configure(final Settings<LoaderSetting> loadersettings, final Settings<LibSetting> libsettings,
            final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apisetting) {
        libsettings.set(LibSetting.DEBUG, true);
        windowSettings.set(WindowSetting.Name, "Raytracer");
    }
    
    @Override
    protected void onInitialized() {
        getResourceManager().instantLoad(false, false, "intern:/de/pcfreak9000/raytracer/");
        getGameController().getGlobalScene().setUpdateableSync(this);
        renderApi = (OpenGLRenderAPI) RenderAPI.get();
        image = (GLFrameBuffer) renderApi.createFrameBuffer(80, 80, 0, 1);
        image.bindFrameBuffer();
        image.assignTarget(0, new FBTarget(TextureFormat.RGBA32, 0));
        image.unbindFrameBuffer();
        batch = new ShadedBatch2D(6);
        computeShader = (GLShader) renderApi.createShader();
        computeShader.create("raytracer");
    }
    
    private GLFrameBuffer image;
    private ShadedBatch2D batch;
    
    private final Matrix3x2f matrix = new Matrix3x2f().translate(-1, -1).scale(2);
    
    private OpenGLRenderAPI renderApi;
    
    private GLShader computeShader;
    
    @Override
    public void update(Time time) {
        computeShader.bindShader();
        image.bindImageTexture(0, 0, 0, false, 0, 0, TextureFormat.RGBA32);
        computeShader.dispatchCompute(image.getWidth()/8, image.getHeight()/8, 1);
        
        
        renderApi.setClearColor(new Color(0, 0, 0, 0));
        renderApi.clear(SurfaceBufferType.Color);
        batch.begin();
        batch.draw(image.getTexture(0), matrix, false, false);
        batch.end();
    }
    
}
