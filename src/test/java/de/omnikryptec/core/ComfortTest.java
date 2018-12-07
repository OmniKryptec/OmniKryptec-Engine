package de.omnikryptec.core;

import de.omnikryptec.core.scene.GameController;
import de.omnikryptec.core.scene.SceneBuilder;
import de.omnikryptec.libapi.LibAPIManager.LibSetting;
import de.omnikryptec.util.settings.Settings;

public class ComfortTest extends EngineLoader {

    public static void main(final String[] args) {
        new ComfortTest().boot();
    }

    @Override
    protected void config(final Settings<LoaderSetting> settings, final Settings<LibSetting> libsettings) {
        libsettings.set(LibSetting.DEBUG, true);
    }

    @Override
    protected void onInitialized(GameController gc) {
        //SceneBuilder builder = gc.getGlobalScene().createBuilder();
        //builder.addGraphicsClearTest();
        SceneBuilder builder = new SceneBuilder();
        builder.addGraphicsClearTest();
        gc.setLocalScene(builder.get());
    }

}
