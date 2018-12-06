package de.omnikryptec.core;

import de.omnikryptec.graphics.display.WindowUpdater;
import de.omnikryptec.libapi.LibAPIManager;
import de.omnikryptec.libapi.exposed.window.OpenGLWindow;
import de.omnikryptec.libapi.exposed.window.OpenGLWindowInfo;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.libapi.opengl.OpenGLUtil.BufferType;
import de.omnikryptec.util.data.Color;

/**
 * a rather advanced way of using this engine.
 *
 * @author pcfreak9000
 *
 */
public class BasicFunctionsTest {

    public static void main(final String[] args) {
        LibAPIManager.init();
        final OpenGLWindow window = new OpenGLWindowInfo().createWindow();
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
