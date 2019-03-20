package de.omnikryptec.core;

import de.codemakers.base.logger.LogLevel;
import de.codemakers.base.logger.Logger;
import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.core.update.ULayer;
import de.omnikryptec.core.update.UpdateableFactory;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.util.Logger.LogType;
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
        windowSettings.set(WindowSetting.Multisample, 16);
        
        //windowSettings.set(WindowSetting.Fullscreen, true);
        //windowSettings.set(WindowSetting.Width, 10000);
        //windowSettings.set(WindowSetting.Height, 2000);
    }
    
    @Override
    protected void onInitialized() {
        OpenGLUtil.setMultisample(true);
        ULayer scene = new ULayer();
        getGameController().getGlobalScene().setUpdateableSync(scene);
        //getResManager().addCallback(LoadingProgressCallback.DEBUG_CALLBACK);
        //getResManager().stage(new AdvancedFile("src/test/resource"));
        getResManager().stage(new AdvancedFile("intern:/de/omnikryptec/resources/")); //TODO Gradle should handle "src/test/resource" internally as the root directory in the jar, "intern:" is the prefix for an intern path and the empty string afterwards should be the root directory, maybe an additional slash ("/") is needed, maybe the additional slash is causing problems...
        //FIXME rename this to getResourceManager() -.- xD
        getResManager().processStaged(false, false);
        //FIXME Alter ich wollte/will das Ressourcen loading machen
        scene.addUpdatable(UpdateableFactory.createScreenClearTest());
        Logger.logDebug("resProv:" + getResourceProvider());
        //builder.addGraphicsBasicImplTest(getResourceProvider().get(TextureData.class, "jd.png"));
        //getGameController().setLocalScene(builder.get());
    }
    
}
