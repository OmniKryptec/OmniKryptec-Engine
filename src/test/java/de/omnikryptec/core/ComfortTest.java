package de.omnikryptec.core;

import de.omnikryptec.graphics.display.WindowUpdater;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.libapi.opengl.OpenGLUtil.BufferType;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.settings.Settings;

public class ComfortTest extends EngineLoader {

    public static void main(String[] args) {
	new ComfortTest().boot();
    }

    @Override
    protected void config(Settings<LoaderSetting> settings) {
	settings.set(LoaderSetting.DEBUG, true);
	settings.set(LoaderSetting.ENGINE_LOOP, new Testloop());
    }

    @Override
    protected void onContextCreationFinish() {

    }

}
