package omnikryptec.display;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

class Window {
    private GLFWFramebufferSizeCallback framebufferSizeCallback;
    private boolean resized = false;
    private long window = 0;
    private int width, height, fwidth, fheight;
    private boolean isfullscreen = false;
    
    Window(String name, GLFWInfo info){
    	width = info.getWidth();
    	height = info.getHeight();
    	GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, info.wantsResizeable() ? GL11.GL_TRUE : GL11.GL_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, info.getMajorVersion());
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, info.getMinorVersion());
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
    	if (info.wantsFullscreen()) {
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            width = vidMode.width();
            height = vidMode.height();
            isfullscreen = true;
        }
    	window = GLFW.glfwCreateWindow(width, height, name, info.wantsFullscreen() ? GLFW.glfwGetPrimaryMonitor() : 0, 0);
        if (window == 0) {
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
    }
    
    long getID(){
    	return window;
    }
    
    void show(){
        GLFW.glfwShowWindow(window);
    }
    
    void dispose(){
        GLFW.glfwDestroyWindow(window);
    }
    
    void swapBuffers(){
    	active = GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_FOCUSED) == GL11.GL_TRUE;
    	resized = false;
        GLFW.glfwSwapBuffers(window);
    }
    
    boolean shouldBeFullscreen(){
    	return isfullscreen;
    }
    
    boolean wasResized(){
    	return resized;
    }
    
    private boolean active=false;
    boolean isActive() {
        return active;
    }
    
    boolean isCloseRequested(){
    	return GLFW.glfwWindowShouldClose(window);
    }
    
    int getWidth(){
    	return width;
    }
    
    int getHeight(){
    	return height;
    }
    
    int getBufferWidth(){
    	return fwidth;
    }
    
    int getBufferHeight(){
    	return fheight;
    }
    
    GLFWFramebufferSizeCallback getDisplaySizeCallback() {
        return framebufferSizeCallback;
    }
    
    private void onResize(int w, int h) {
        width = w;
        height = h;
        resized = true;
        IntBuffer framebufferWidth = BufferUtils.createIntBuffer(1),
                framebufferHeight = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetFramebufferSize(window, framebufferWidth, framebufferHeight);
        fwidth = framebufferWidth.get();
        fheight = framebufferHeight.get();
    }
}
