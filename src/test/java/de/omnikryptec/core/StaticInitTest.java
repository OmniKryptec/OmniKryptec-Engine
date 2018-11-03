package de.omnikryptec.core;

import de.omnikryptec.graphics.display.WindowUpdater;
import de.omnikryptec.libapi.glfw.OpenGLWindowInfo;
import de.omnikryptec.libapi.glfw.Window;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.libapi.opengl.OpenGLUtil.BufferType;
import de.omnikryptec.util.AbstractUpdater;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.settings.Settings;

public class StaticInitTest {

    public static void main(String[] args) {
	EngineLoader.initialize();
	EngineLoader.setConfiguration(new Settings<>());
	Window<?> window = new OpenGLWindowInfo().createWindow();
	window.setVisible(true);
	WindowUpdater updater = new WindowUpdater(window);
	while (!window.isCloseRequested()) {
	    updater.update(0);
	    if (updater.getFrameCount() % 40 == 0) {
		OpenGLUtil.setClearColor(Color.randomRGB());
	    }
	    OpenGLUtil.clear(BufferType.COLOR);
	}
    }

}
