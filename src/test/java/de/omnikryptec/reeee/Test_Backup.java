package de.omnikryptec.reeee;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.function.Consumer;

import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

import de.omnikryptec.core.Omnikryptec;
import de.omnikryptec.core.Scene;
import de.omnikryptec.core.Omnikryptec.LoaderSetting;
import de.omnikryptec.demo.RendererDemo;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.IndexBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderAPI.BufferUsage;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.VertexArray;
import de.omnikryptec.libapi.exposed.render.VertexBuffer;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.libapi.exposed.render.shader.Shader;
import de.omnikryptec.libapi.exposed.render.shader.UniformMatrix;
import de.omnikryptec.libapi.exposed.window.IWindow;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.libapi.exposed.window.WindowUpdater;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.batch.SimpleBatch2D;
import de.omnikryptec.render.batch.SimpleShaderSlot;
import de.omnikryptec.render.objects.SimpleSprite;
import de.omnikryptec.render.renderer.Renderer;
import de.omnikryptec.render.renderer.Renderer2D;
import de.omnikryptec.render.renderer.ViewManager;
import de.omnikryptec.render.renderer.ViewManager.EnvironmentKey;
import de.omnikryptec.render2.Batch2D;
import de.omnikryptec.render2.RenderedBaseRenderer;
import de.omnikryptec.render2.SimpleBatchedShader;
import de.omnikryptec.render2.SimpleBatchedShader.SimpleData2D;
import de.omnikryptec.resource.MeshData.Primitive;
import de.omnikryptec.resource.loadervpc.ResourceManager;
import de.omnikryptec.resource.parser.shader.ShaderParser;
import de.omnikryptec.util.Logger;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.profiling.Profiler;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class Test_Backup extends Omnikryptec {
    
    public static void main(final String[] args) {
        Omnikryptec.initialize(new Settings<>(), RenderAPI.OpenGL, new Settings<>(), new Settings<>(), false, false);
        final IWindow window = LibAPIManager.instance().getGLFW().getRenderAPI().getWindow();
        window.setVisible(true);
        final WindowUpdater updater = new WindowUpdater(window);
        final Color clearColor = new Color();
        RenderedBaseRenderer rbr = new RenderedBaseRenderer();
        SimpleBatchedShader sbs = new SimpleBatchedShader();
        Batch2D batch = new Batch2D();
        SimpleData2D[] ar = new SimpleData2D[10000];
        for (int i = 0; i < ar.length; i++) {
            SimpleData2D s2d = sbs.createRenderData();
            Matrix3x2f trans = new Matrix3x2f();
            trans.setTranslation(-0.5f, -0.5f);
            trans.rotateLocal(3.14f / 4 + i);
            s2d.posModule.setTransform(trans, 1, 1);
            s2d.colorModule.color().set(1, ((i % 100) / 100f), 0, 1);
            ar[i] = s2d;
        }
        Matrix3x2f[] tr = new Matrix3x2f[10000];
        for (int i = 0; i < tr.length; i++) {
            Matrix3x2f trans = new Matrix3x2f();
            trans.setTranslation(-0.5f, -0.5f);
            trans.rotateLocal(3.14f / 4 + i);
            tr[i] = trans;
        }
        SimpleBatch2D b = new SimpleBatch2D(10000);
        b.color().set(1, 0, 0);
        ((SimpleShaderSlot)b.getShaderSlot()).setViewProjectionMatrix(new Matrix4f());
        Profiler.setEnabled(true);
        ResourceManager rm = new ResourceManager();
        rm.addDefaultLoaders();
        rm.load(false, false, "intern:/de/omnikryptec/resources/glslmodules/shader2d.glsl");
        while (!window.isCloseRequested()) {
            LibAPIManager.instance().getGLFW().getRenderAPI().getSurface().clearColor(clearColor);
            //batch.begin(rbr);
            b.begin();
            Profiler.begin("old");
            for (Matrix3x2f m : tr) {
                b.drawRect(m, 1, 1);
            }
            //            for (SimpleData2D s2d : ar) {
            //                batch.draw(s2d);
            //                
            //            }
            b.end();
            //batch.end();
            Profiler.end();
            updater.update();
        }
        System.out.println(Profiler.currentInfo());
        Omnikryptec.deinitialize();
    }
    
    //    public static void main(final String[] args) {
    //        new Test().start();
    //    }
    
    @Override
    protected void configure(final Settings<LoaderSetting> loadersettings, final Settings<LibSetting> libsettings,
            final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apisetting,
            final KeySettings keys) {
        libsettings.set(LibSetting.DEBUG, false);
        libsettings.set(LibSetting.LOGGING_MIN, Logger.LogType.Debug);
        windowSettings.set(WindowSetting.Name, "Fucking TEST");
    }
    
    @Override
    protected void onInitialized() {
        RenderedBaseRenderer rbr = new RenderedBaseRenderer();
        SimpleBatchedShader sbs = new SimpleBatchedShader();
        Batch2D batch = new Batch2D();
        SimpleData2D s2d = sbs.createRenderData();
        s2d.posModule.setTransform(-1000, -1000, 2000, 2000);
        s2d.colorModule.color().set(1, 0, 0, 1);
        
        //Create the rendering environment
        final Scene scene = getGame().createAndAddScene();
        scene.getViewManager().addRenderer(new Renderer() {
            
            @Override
            public void render(ViewManager viewManager, RenderAPI api, IProjection projection, FrameBuffer target,
                    Settings<EnvironmentKey> envSettings, Time time) {
                batch.begin(rbr);
                batch.draw(s2d);
                batch.end();
            }
        });
    }
    
}