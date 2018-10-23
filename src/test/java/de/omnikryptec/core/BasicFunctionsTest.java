package de.omnikryptec.core;

import de.omnikryptec.graphics.display.WindowUpdater;
import de.omnikryptec.libapi.glfw.GLFWManager;
import de.omnikryptec.libapi.glfw.OpenGLWindow;
import de.omnikryptec.libapi.glfw.OpenGLWindowInfo;
import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.libapi.opengl.OpenGLUtil.BufferType;
import de.omnikryptec.util.data.Color;

/**
 * a rather advanced way of using this engine.
 * @author pcfreak9000
 *
 */
public class BasicFunctionsTest {

	public static void main(String[] args) {
		GLFWManager.init();
		OpenGLWindow window = new OpenGLWindow(new OpenGLWindowInfo());
		window.show();
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
