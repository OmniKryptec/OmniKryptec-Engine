package de.omnikryptec.core;

import de.omnikryptec.util.settings.Settings;

public class ComfortTest extends EngineLoader {

    public static void main(final String[] args) {
        new ComfortTest().boot();
    }

    @Override
    protected void config(final Settings<LoaderSetting> settings) {
        settings.set(LoaderSetting.DEBUG, true);
        settings.set(LoaderSetting.ENGINE_LOOP, new Testloop());
    }

    @Override
    protected void onContextCreationFinish() {

    }

}
