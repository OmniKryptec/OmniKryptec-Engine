package de.omnikryptec.core;

import de.omnikryptec.core.scene.GameController;
import de.omnikryptec.core.scene.SceneBuilder;
import de.omnikryptec.libapi.exposed.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.exposed.window.Window.WindowSetting;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;

public class ComfortTest extends EngineLoader {
    
    public static void main(final String[] args) {
        new ComfortTest().start();
    }
    
    @Override
    protected void config(final Settings<LoaderSetting> loadersettings, final Settings<LibSetting> libsettings,
            final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apisettings) {
        libsettings.set(LibSetting.DEBUG, false);
    }
    
    @Override
    protected void onInitialized(final GameController gc) {
        //SceneBuilder builder = gc.getGlobalScene().createBuilder();
        //builder.addGraphicsClearTest();
        final SceneBuilder builder = new SceneBuilder();
        builder.addGraphicsClearTest();
        gc.setLocalScene(builder.get());
    }
    
}
