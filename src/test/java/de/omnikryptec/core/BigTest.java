package de.omnikryptec.core;

import java.awt.image.renderable.RenderContext;
import java.util.Objects;

import org.joml.Vector2f;

import de.codemakers.base.logger.LogLevel;
import de.codemakers.base.logger.Logger;
import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.core.update.ULayer;
import de.omnikryptec.core.update.UpdateableFactory;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.render.batch.ReflectedShaderSlot;
import de.omnikryptec.render.objects.ReflectiveSprite;
import de.omnikryptec.render.objects.RenderedObjectManager;
import de.omnikryptec.render.objects.ReflectiveSprite.Reflection2DType;
import de.omnikryptec.render.renderer.LocalRendererContext;
import de.omnikryptec.render.renderer.ReflectedRenderer2D;
import de.omnikryptec.render.renderer.RendererContext;
import de.omnikryptec.resource.TextureData;
import de.omnikryptec.util.Profiler;
import de.omnikryptec.util.Logger.LogType;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;

public class BigTest extends EngineLoader {
    
    public static void main(final String[] args) {
        Logger.getDefaultAdvancedLeveledLogger().setMinimumLogLevel(LogLevel.FINEST);
        Logger.getDefaultAdvancedLeveledLogger().createLogFormatBuilder().appendLogLevel().appendText(": ")
                .appendObject().appendNewLine().appendThread().appendLocation().appendNewLine()
                .finishWithoutException();
        AdvancedFile.DEBUG = true;
        AdvancedFile.DEBUG_TO_STRING = true;
        new BigTest().start();
    }
    
    @Override
    protected void configure(final Settings<LoaderSetting> loadersettings, final Settings<LibSetting> libsettings,
            final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apisettings) {
        libsettings.set(LibSetting.DEBUG, true);
        libsettings.set(LibSetting.LOGGING_MIN, LogType.Debug);
        windowSettings.set(WindowSetting.Name, "BigTest-Window");
        windowSettings.set(WindowSetting.LockAspectRatio, true);
        windowSettings.set(WindowSetting.VSync, false);
        
        //windowSettings.set(WindowSetting.Fullscreen, true);
        //windowSettings.set(WindowSetting.Width, 10000);
        //windowSettings.set(WindowSetting.Height, 2000);
    }
    
    @Override
    protected void onInitialized() {
        ULayer scene = new ULayer();
        getGameController().getGlobalScene().setUpdateableSync(scene);
        getResourceManager().stage("intern:/de/omnikryptec/resources/");
        getResourceManager().processStaged(false, false);
        //scene.addUpdatable(UpdateableFactory.createScreenClearTest());
        //scene.addUpdatable(UpdateableFactory.createRenderTest(getTextures()));
        RendererContext context = new RendererContext();
        scene.addUpdatable(context);
        LocalRendererContext c = context.createLocal();
        c.addRenderer(new ReflectedRenderer2D());
        ReflectiveSprite s = new ReflectiveSprite();
        //s.setColor(new Color(1, 0, 0));
        s.setReflectionType(Reflection2DType.Cast);
        s.setPosition(new Vector2f(0.5f));
        s.setWidth(0.25f);
        s.setHeight(0.25f);
        s.setLayer(1);
        s.setTexture(getTextures().get("jd.png"));
        ReflectiveSprite back = new ReflectiveSprite();
        back.setTexture(getTextures().get("jxcvxcvxcvxcvn.png"));
        back.setPosition(new Vector2f());
        back.setReflectionType(Reflection2DType.Receive);
        back.reflectiveness().set(0.5f, 0.5f, 0.5f);
        c.getIRenderedObjectManager().add(back);
        c.getIRenderedObjectManager().add(s);
    }
    
    @Override
    protected void onShutdown() {
        System.out.println(Profiler.currentInfo());
    }
}
