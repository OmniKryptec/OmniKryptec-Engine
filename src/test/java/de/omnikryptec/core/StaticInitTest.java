package de.omnikryptec.core;

import de.omnikryptec.graphics.display.WindowUpdater;
import de.omnikryptec.libapi.exposed.window.OpenGLWindowInfo;
import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.libapi.opengl.OpenGLUtil.BufferType;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.settings.Settings;

public class StaticInitTest {

    public static void main(final String[] args) {
        EngineLoader.initialize();
        EngineLoader.setConfiguration(new Settings<>());
        final Window<?> window = new OpenGLWindowInfo().createWindow();
        window.setVisible(true);
        final WindowUpdater updater = new WindowUpdater(window);
        while (!window.isCloseRequested()) {
            updater.update(0);
            if (updater.getOperationCount() % 40 == 0) {
                OpenGLUtil.setClearColor(Color.randomRGB());
            }
            OpenGLUtil.clear(BufferType.COLOR);
        }
    }

}
