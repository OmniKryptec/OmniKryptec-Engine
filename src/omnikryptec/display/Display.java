package omnikryptec.display;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class Display {
	
	private static long window=0;
	private static int width,height;
	private static GLFWErrorCallback errorCallback;
	private static GLFWFramebufferSizeCallback framebufferSizeCallback;
	private static boolean resized=false;
	
	private static long lastsynced;
	
	static void create(String name, GLFWInfo info){
		width = info.getWidth();
		height = info.getHeight();
		GLFW.glfwInit();
		GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, info.wantsResizeable()?GL11.GL_TRUE:GL11.GL_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, info.getMajorVersion());
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, info.getMinorVersion());
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE); 
		window = GLFW.glfwCreateWindow(width, height, name, 0, 0);
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
		lastsynced = getCurrentTime();
	}
	
	private static void onResize(int w, int h){
		width = w;
		height = h;
		resized = true;
	}
	public static boolean wasResized(){
		return resized;
	}
	
	static void update(){
		resized = false;
		GLFW.glfwPollEvents();
		GLFW.glfwSwapBuffers(window);
	}
	
	
	static void destroy(){
		GLFW.glfwDestroyWindow(window);
		GLFW.glfwTerminate();
	}
	
	static final long getCurrentTime() { //TODO do microseconds
		return (long) (GLFW.glfwGetTime()*1000);
	}
	
	static void sync(int fps) {
        long target = lastsynced + (long)(1000.0/fps) + 1000;
        try {
            while (getCurrentTime() < target) {
                Thread.sleep(1);
            }
        }
        catch (InterruptedException ignore) {}
        lastsynced = getCurrentTime();
    }
	
	public static boolean isCloseRequested(){
		return GLFW.glfwWindowShouldClose(window);
	}
	
	public static int getWidth(){
		return 0;
	}
	
	public static int getHeight(){
		return 0;
	}
	
}
