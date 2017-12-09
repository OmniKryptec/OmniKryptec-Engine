package omnikryptec.display;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;

import omnikryptec.event.input.InputManager;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public class Display {

	private static GLFWErrorCallback errorCallback;
	private static Window window;
	private static double lastsynced;
	private static final int[] viewport = new int[4];
	private static double aspectratio;

	static void create(String name, GLFWInfo info) {
		GLFW.glfwInit();
		GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(Logger.NEWSYSERR));
		window = new Window(name, info);
		calculateViewport();
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
		return viewport[2];
	}

	public static int getHeight() {
		return viewport[3];
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

	public static final void setDisplayViewport() {
		calculateViewport();
		GL11.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
	}

	public static final int[] getViewportData() {
		return viewport;
	}

	public static final void resetAspectRatio() {
		setAspectRatio(-1, true);
	}

	static final void calculateViewport() {
		double winV = (double) window.getWidth() / (double) window.getHeight();
		viewport[0] = 0;
		viewport[1] = 0;
		viewport[2] = window.getWidth();
		viewport[3] = window.getHeight();
		if (aspectratio > 0) {
			if (winV <= aspectratio) {
				viewport[3] = (int) (window.getHeight()/winV * aspectratio);
				viewport[1] = (int) ((window.getHeight() - viewport[3]) * 0.5);
			} else {
				viewport[2] = (int) (window.getWidth()/winV * aspectratio);
				viewport[0] = (int) ((window.getWidth() - viewport[2]) * 0.5);
			}
		}
	}

	public static final void setAspectRatio(double a, boolean set) {
		aspectratio = a;
		if (set) {
			setDisplayViewport();
		} else {
			calculateViewport();
		}
	}
}
