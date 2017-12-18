package omnikryptec.display;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import omnikryptec.event.input.InputManager;
import omnikryptec.graphics.OpenGL;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public class Display {

	private static GLFWErrorCallback errorCallback;
	private static Window window;
	private static double lastsynced;
	private static int[] viewport = new int[4];
	private static double aspectratio=-1;

	static void create(String name, GLFWInfo info) {
		GLFW.glfwInit();
		GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(Logger.NEWSYSERR));
		window = new Window(name, info);
		if (info.lockWindowAspectRatio()[0] > 0 && info.lockWindowAspectRatio()[1] > 0) {
			GLFW.glfwSetWindowAspectRatio(window.getID(), info.lockWindowAspectRatio()[0],
					info.lockWindowAspectRatio()[1]);
		}
		calcViewport();
		setARViewPort();
		InputManager.initCallbacks();
		lastsynced = getCurrentTime();
		Logger.log("Successfully created GLContext and the Window!", LogLevel.FINEST);
	}

	static GLFWErrorCallback getErrorCallback() {
		return errorCallback;
	}

	public static boolean shouldBeFullscreen() {
		return window.shouldBeFullscreen();
	}

	public static boolean wasResized() {
		return window.wasResized();
	}

	static void update() {
		window.swapBuffers();
		GLFW.glfwPollEvents();
		if (wasResized()) {
			calcViewport();
			setARViewPort();
		}
	}

	static void destroy() {
		window.dispose();
		InputManager.closeCallbacks();
		GLFW.glfwTerminate();
	}

	static final double getCurrentTime() {
		return GLFW.glfwGetTime() * 1000;
	}

	static void sync(int fps) {
		double target = lastsynced + (1000.0 / fps);
		try {
			while ((lastsynced = getCurrentTime()) < target) {
				Thread.sleep(1);
			}
		} catch (InterruptedException ex) {
		}
	}

	public static boolean isCloseRequested() {
		return window.isCloseRequested();
	}

	public static boolean isActive() {
		return window.isActive();
	}

	public static int getWidth() {
		return getBufferWidth();
		//return viewport[2];
	}

	public static int getHeight() {
		return getBufferHeight();
		//return viewport[3];
	}

	public static int getBufferWidth() {
		return window.getBufferWidth();
	}

	public static int getBufferHeight() {
		return window.getBufferHeight();
	}

	public static final long getID() {
		return window.getID();
	}

	public static final void show() {
		window.show();
	}

	public static final void resetViewport() {
		OpenGL.gl11viewport(0, 0, getBufferWidth(), getBufferHeight());
	}

	public static final void setARViewPort() {
		//resetViewport();
		OpenGL.gl11viewport(viewport);
	}
	
	public static final void resetAspectRatio() {
		setAspectRatio(-1, true);
	}

	public static final int[] calculateViewport(int w, int h) {
		int[] viewport = new int[4];
		viewport[0] = 0;
		viewport[1] = 0;
		viewport[2] = w;
		viewport[3] = h;
		if (aspectratio > 0) {
			if ((double) w / (double) h <= aspectratio) {
				viewport[3] = (int) (w * (1.0 / aspectratio));
				viewport[1] = (int) ((h - viewport[3]) * 0.5);
			} else {
				viewport[2] = (int) (h * aspectratio);
				viewport[0] = (int) ((w - viewport[2]) * 0.5);
			}
		}
		return viewport;
	}

	private static final void calcViewport() {
		viewport = calculateViewport(getBufferWidth(), getBufferHeight());
	}
	
	public static double getAspectRatio() {
		return aspectratio;
	}
	
	public static int[] getViewportdata() {
		return viewport;
	}
	
	public static final void setAspectRatio(double a, boolean set) {
		aspectratio = a;
		calcViewport();
		setARViewPort();
	}
	
}
