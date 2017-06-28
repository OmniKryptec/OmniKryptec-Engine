package omnikryptec.display;

import omnikryptec.input.InputManager;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import omnikryptec.logger.Logger;
import omnikryptec.logger.LogEntry.LogLevel;

public class Display {


    private static GLFWErrorCallback errorCallback;
    private static Window window;
    private static double lastsynced;

    static void create(String name, GLFWInfo info) {
        GLFW.glfwInit();
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(Logger.NEWSYSERR));
        window = new Window(name, info);
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

    public static void update() {
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
//        long target = lastsynced + (long)(1000.0/fps) + 1000;
//        try {
//            while (getCurrentTime() < target) {
//                Thread.sleep(1);
//            }
//        }
//        catch (InterruptedException ignore) {}
//        lastsynced = getCurrentTime();
    }

    public static boolean isCloseRequested() {
        return window.isCloseRequested();
    }

    public static boolean isActive(){
    	return window.isActive();
    }
    
    public static int getWidth() {
        return window.getWidth();
    }

    public static int getHeight() {
        return window.getHeight();
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

}
