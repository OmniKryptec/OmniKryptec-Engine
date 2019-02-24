package de.omnikryptec.libapi.opengl;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import de.omnikryptec.event.EventBus;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.window.WindowEvent;
import de.omnikryptec.libapi.exposed.window.WindowInterfaceWIP;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.libapi.opengl.framebuffer.GLScreenBuffer;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;

public class GLWindow implements WindowInterfaceWIP {
    
    private final long windowId;
    private final GLScreenBuffer screenBuffer;
    
    private int windowWidth;
    private int windowHeight;
    private boolean isFullscreen;
    private boolean isActive;
    
    public GLWindow(Settings<WindowSetting> windowSettings, Settings<IntegerKey> apiSettings) {
        Util.ensureNonNull(windowSettings, "Window settings must not be null!");
        this.windowWidth = windowSettings.get(WindowSetting.Width);
        this.windowHeight = windowSettings.get(WindowSetting.Height);
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE,
                (boolean) windowSettings.get(WindowSetting.Resizeable) ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        final int mav = apiSettings.get(OpenGLRenderAPI.MAJOR_VERSION);
        final int miv = apiSettings.get(OpenGLRenderAPI.MINOR_VERSION);
        if (mav > 3 || (mav > 2 && miv > 1)) {
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, mav);
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, miv);
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        }
        if ((boolean) windowSettings.get(WindowSetting.Fullscreen)) {
            //final GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            //this.width = vidMode.width();
            //this.height = vidMode.height();
            this.isFullscreen = true;
        }
        this.windowId = GLFW.glfwCreateWindow(this.windowWidth, this.windowHeight,
                (String) windowSettings.get(WindowSetting.Name),
                (boolean) windowSettings.get(WindowSetting.Fullscreen) ? GLFW.glfwGetPrimaryMonitor() : 0, 0);
        if (this.windowId == 0) {
            throw new RuntimeException("Failed to create window");
        }
        double aspectRatio = -1;
        if ((boolean) windowSettings.get(WindowSetting.LockAspectRatio)
                && (boolean) windowSettings.get(WindowSetting.Resizeable)) {
            GLFW.glfwSetWindowAspectRatio(this.windowId, this.windowWidth, this.windowHeight);
            aspectRatio = this.windowWidth / (double) this.windowHeight;
        }
        registerCallbacks();
        GLFW.glfwMakeContextCurrent(windowId);
        GL.createCapabilities();
        setVSync(windowSettings.get(WindowSetting.VSync));
        screenBuffer = new GLScreenBuffer(windowId, aspectRatio);
        screenBuffer.bindFrameBuffer();
    }
    
    private void registerCallbacks() {
        final EventBus windowBus = LibAPIManager.LIB_API_EVENT_BUS;
        GLFW.glfwSetWindowSizeCallback(this.windowId,
                (window, width, height) -> windowBus.post(new WindowEvent.WindowResized(this, width, height)));
        GLFW.glfwSetWindowFocusCallback(this.windowId,
                (window, focused) -> windowBus.post(new WindowEvent.WindowFocused(this, focused)));
        GLFW.glfwSetWindowIconifyCallback(this.windowId,
                (window, iconified) -> windowBus.post(new WindowEvent.WindowIconified(this, iconified)));
        GLFW.glfwSetWindowMaximizeCallback(this.windowId,
                (window, maximized) -> windowBus.post(new WindowEvent.WindowMaximized(this, maximized)));
    }
    
    @Override
    public boolean isActive() {
        return isActive;
    }
    
    @Override
    public boolean isFullscreen() {
        return isFullscreen;
    }
    
    @Override
    public boolean isCloseRequested() {
        return GLFW.glfwWindowShouldClose(windowId);
    }
    
    @Override
    public int getWindowWidth() {
        return windowWidth;
    }
    
    @Override
    public int getWindowHeight() {
        return windowHeight;
    }
    
    @Override
    public FrameBuffer getDefaultFrameBuffer() {
        return screenBuffer;
    }
    
    @Override
    public void setVSync(boolean vsync) {
        GLFW.glfwSwapInterval(vsync ? 1 : 0);
    }
    
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            GLFW.glfwShowWindow(this.windowId);
        } else {
            GLFW.glfwHideWindow(this.windowId);
        }
    }
    
    @Override
    public void dispose() {
        GLFW.glfwDestroyWindow(windowId);
    }
    
    @Override
    public void swapBuffers() {
        GLFW.glfwSwapBuffers(this.windowId);
    }
    
    @Override
    public long getID() {
        return windowId;
    }
    
}
