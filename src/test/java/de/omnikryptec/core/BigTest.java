package de.omnikryptec.core;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.core.scene.SceneBuilder;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.window.Window.WindowSetting;
import de.omnikryptec.resource.loadervpc.LoadingProgressCallback;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;

public class BigTest extends EngineLoader {
    public static void main(final String[] args) {
        new BigTest().start();
    }
    
    @Override
    protected void configure(final Settings<LoaderSetting> loadersettings, final Settings<LibSetting> libsettings,
            final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apisettings) {
        libsettings.set(LibSetting.DEBUG, true);
        windowSettings.set(WindowSetting.Name, "BigTest-Window");
    }
    
    @Override
    protected void onInitialized() {
        final SceneBuilder builder = getGameController().getGlobalScene().createBuilder();
        getResManager().addCallback(LoadingProgressCallback.DEBUG_CALLBACK);
        getResManager().instantLoad(false, new AdvancedFile("src/test/resources"));
        
        builder.addGraphicsClearTest();
        builder.addGraphicsBasicImplTest();
        
        //getGameController().setLocalScene(builder.get());
    }
}
