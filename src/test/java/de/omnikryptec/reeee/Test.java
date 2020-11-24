package de.omnikryptec.reeee;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.function.Consumer;

import de.omnikryptec.core.Omnikryptec;
import de.omnikryptec.core.Scene;
import de.omnikryptec.core.Omnikryptec.LoaderSetting;
import de.omnikryptec.demo.RendererDemo;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.window.IWindow;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.libapi.exposed.window.WindowUpdater;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.batch.SimpleBatch2D;
import de.omnikryptec.render.objects.SimpleSprite;
import de.omnikryptec.render.renderer.Renderer;
import de.omnikryptec.render.renderer.Renderer2D;
import de.omnikryptec.render.renderer.ViewManager;
import de.omnikryptec.render.renderer.ViewManager.EnvironmentKey;
import de.omnikryptec.render2.Batch2D;
import de.omnikryptec.render2.RenderedBaseRenderer;
import de.omnikryptec.render2.SimpleBatchedShader;
import de.omnikryptec.render2.SimpleBatchedShader.SimpleData2D;
import de.omnikryptec.util.Logger;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.profiling.Profiler;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.KeySettings;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class Test extends Omnikryptec {
    
    
    public static void main(final String[] args) {
        Omnikryptec.initialize(new Settings<>(), RenderAPI.OpenGL, new Settings<>(), new Settings<>(), false, false);
        final IWindow window = LibAPIManager.instance().getGLFW().getRenderAPI().getWindow();
        window.setVisible(true);
        final WindowUpdater updater = new WindowUpdater(window);
        final Color clearColor = new Color();
        RenderedBaseRenderer rbr = new RenderedBaseRenderer();
        SimpleBatchedShader sbs = new SimpleBatchedShader();
        Batch2D batch = new Batch2D();
        SimpleData2D s2d = sbs.createRenderData();
        s2d.posModule.setTransform(-1000, -1000, 2000, 2000);
        s2d.colorModule.color().set(1, 0, 0, 1);
        SimpleBatch2D b = new SimpleBatch2D(10000);
        b.color().set(1, 0, 0);
        Profiler.setEnabled(true);
        while (!window.isCloseRequested()) {
            LibAPIManager.instance().getGLFW().getRenderAPI().getSurface().clearColor(clearColor);
            batch.begin(rbr);
            //b.begin();
            Profiler.begin("old");
            for (int i = 0; i < 1000; i++) {
                s2d.posModule.setTransform(-1000, -1000, 2000, 2000);
                batch.draw(s2d);
               // b.drawRect(-1000 + i, -1000, 2000, 2000);
            }
            //b.end();
            batch.end();
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
