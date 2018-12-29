package de.omnikryptec.core;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.libapi.exposed.window.WindowUpdater;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.libapi.opengl.OpenGLUtil.BufferType;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.settings.Settings;

/**
 * a rather advanced way of using this engine.
 *
 * @author pcfreak9000
 *
 */
public class BasicFunctionsTest {
    
    public static void main(final String[] args) {
        LibAPIManager.init(new Settings<>());
        LibAPIManager.active().setRenderer(RenderAPI.OpenGL, new Settings<>());
        final Window window = LibAPIManager.active().getRenderAPI().createWindow(new Settings<>());
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
