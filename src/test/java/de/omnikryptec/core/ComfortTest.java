package de.omnikryptec.core;

import de.omnikryptec.libapi.LibAPIManager.LibSetting;
import de.omnikryptec.util.settings.Settings;

public class ComfortTest extends EngineLoader {

    public static void main(final String[] args) {
        new ComfortTest().boot();
    }

    @Override
    protected void config(final Settings<LoaderSetting> settings, final Settings<LibSetting> libsettings) {
        libsettings.set(LibSetting.DEBUG, true);
        settings.set(LoaderSetting.ENGINE_LOOP, new Testloop());
    }

    @Override
    protected void onContextCreationFinish() {

    }

}
