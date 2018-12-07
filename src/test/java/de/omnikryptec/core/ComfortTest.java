package de.omnikryptec.core;

import de.omnikryptec.core.scene.GameController;
import de.omnikryptec.libapi.LibAPIManager.LibSetting;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.libapi.opengl.OpenGLUtil.BufferType;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

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
        gc.getGlobalScene().addUpdateable(new Updateable() {
            public void update(Time time) {
                if (time.opsCount % 40 == 0) {
                    OpenGLUtil.setClearColor(Color.randomRGB());
                }
                OpenGLUtil.clear(BufferType.COLOR);
            }
        });
    }

}
