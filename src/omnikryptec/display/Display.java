package omnikryptec.display;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import omnikryptec.logger.Logger;
import omnikryptec.logger.LogEntry.LogLevel;

public class Display {
	
	private static long window=0;
	private static int width,height,fwidth,fheight;
	private static GLFWErrorCallback errorCallback;
	private static GLFWFramebufferSizeCallback framebufferSizeCallback;
	private static boolean resized=false;
	
	private static boolean isfullscreen=false;
	private static double lastsynced;
	
	static void create(String name, GLFWInfo info){
		width = info.getWidth();
		height = info.getHeight();
		GLFW.glfwInit();
		GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(Logger.NEWSYSERR));
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, info.wantsResizeable()?GL11.GL_TRUE:GL11.GL_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, info.getMajorVersion());
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, info.getMinorVersion());
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE); 
		if(info.wantsFullscreen()){
			 GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		     width = vidMode.width();
		     height = vidMode.height();
		     isfullscreen = true;
		}
		window = GLFW.glfwCreateWindow(width, height, name, info.wantsFullscreen()?GLFW.glfwGetPrimaryMonitor():0, 0);
		if(window == 0) {
		    throw new RuntimeException("Failed to create window");
		}
		GLFW.glfwSetFramebufferSizeCallback(window, (framebufferSizeCallback = new GLFWFramebufferSizeCallback() {
		    @Override
		    public void invoke(long window, int width, int height) {
		        onResize(width, height);
		    }
		}));
		GLFW.glfwMakeContextCurrent(window);
		GL.createCapabilities();
		GLFW.glfwSwapInterval(1);
		lastsynced = getCurrentTime();
		GLFW.glfwShowWindow(window);
		Logger.log("Successfully created GLContext and the Window!", LogLevel.FINEST);
	}
	
	public static boolean shouldBeFullscreen(){
		return isfullscreen;
	}
	
	static GLFWErrorCallback getErrorCallback(){
		return errorCallback;
	}
	
	static GLFWFramebufferSizeCallback getDisplaySizeCallback(){
		return framebufferSizeCallback;
	}
	
	private static void onResize(int w, int h){
		width = w;
		height = h;
		resized = true;
	}
	public static boolean wasResized(){
		return resized;
	}
	
	public static boolean isActive(){
		return GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_FOCUSED)==GL11.GL_TRUE;
	}
	
	public static void update(){
		resized = false;
		GLFW.glfwPollEvents();
		GLFW.glfwSwapBuffers(window);
	}
	
	
	static void destroy(){
		GLFW.glfwDestroyWindow(window);
		GLFW.glfwTerminate();
	}
	
	static final double getCurrentTime() {
		return GLFW.glfwGetTime()*1000;
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
	
	public static boolean isCloseRequested(){
		return GLFW.glfwWindowShouldClose(window);
	}
	
	public static int getWidth(){
		return width;
	}
	
	public static int getHeight(){
		return height;
	}
	
}
