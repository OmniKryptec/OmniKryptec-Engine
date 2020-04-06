/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.libapi.opengl;

import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import de.omnikryptec.event.EventBus;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.input.InputEvent;
import de.omnikryptec.libapi.exposed.render.FrameBufferStack;
import de.omnikryptec.libapi.exposed.window.IWindow;
import de.omnikryptec.libapi.exposed.window.WindowEvent;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.libapi.opengl.framebuffer.GLScreenBuffer;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.math.MathUtil;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;

public class GLWindow implements IWindow {
    private final EventBus windowBus = LibAPIManager.ENGINE_EVENTBUS;
    
    private final long windowId;
    private final GLScreenBuffer screenBuffer;
    
    private int windowWidth;
    private int windowHeight;
    private boolean isFullscreen;
    private boolean isActive;
    
    public GLWindow(final Settings<WindowSetting> windowSettings, final Settings<IntegerKey> apiSettings,
            final FrameBufferStack fbStack) {
        Util.ensureNonNull(windowSettings, "Window settings must not be null!");
        this.windowWidth = windowSettings.get(WindowSetting.Width);
        this.windowHeight = windowSettings.get(WindowSetting.Height);
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE,
                (boolean) windowSettings.get(WindowSetting.Resizeable) ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        int mav = apiSettings.get(OpenGLRenderAPI.MAJOR_VERSION);
        int miv = apiSettings.get(OpenGLRenderAPI.MINOR_VERSION);
        if (mav > 3 || (mav == 3 && miv >= 3)) {
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, mav);
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, miv);
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        }
        if (LibAPIManager.debug()) {
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);
        }
        if (!(boolean) windowSettings.get(WindowSetting.Decorated)) {
            GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
        }
        GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, GLFW.GLFW_TRUE);
        //Does this even work:
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);
        if ((boolean) windowSettings.get(WindowSetting.Fullscreen)) {
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
        GLFW.glfwMakeContextCurrent(this.windowId);
        GL.createCapabilities();
        setVSync(windowSettings.get(WindowSetting.VSync));
        this.screenBuffer = new GLScreenBuffer(this.windowId, aspectRatio, fbStack);
        this.windowBus.register(this.screenBuffer);
    }
    
    private void registerCallbacks() {
        GLFW.glfwSetWindowSizeCallback(this.windowId,
                (window, width, height) -> this.windowBus.post(new WindowEvent.WindowResized(this, width, height)));
        GLFW.glfwSetFramebufferSizeCallback(this.windowId, (window, width, height) -> this.windowBus
                .post(new WindowEvent.ScreenBufferResizedNative(this, width, height, this.screenBuffer)));
        GLFW.glfwSetWindowFocusCallback(this.windowId,
                (window, focused) -> this.windowBus.post(new WindowEvent.WindowFocused(this, focused)));
        GLFW.glfwSetWindowIconifyCallback(this.windowId,
                (window, iconified) -> this.windowBus.post(new WindowEvent.WindowIconified(this, iconified)));
        GLFW.glfwSetWindowMaximizeCallback(this.windowId,
                (window, maximized) -> this.windowBus.post(new WindowEvent.WindowMaximized(this, maximized)));
        GLFW.glfwSetKeyCallback(this.windowId, (window, key, scancode, action, mods) -> this.windowBus
                .post(new InputEvent.KeyEvent(key, scancode, action, mods)));
        GLFW.glfwSetMouseButtonCallback(this.windowId, (window, button, action, mods) -> this.windowBus
                .post(new InputEvent.MouseButtonEvent(button, action, mods)));
        GLFW.glfwSetCursorPosCallback(this.windowId,
                (window, xpos, ypos) -> this.windowBus.post(new InputEvent.MousePositionEvent(xpos, ypos, MathUtil
                        .relativeMousePosition(xpos, ypos, this.screenBuffer.getViewportUnsafe(), new Vector2f()))));
        GLFW.glfwSetScrollCallback(this.windowId,
                (window, x, y) -> this.windowBus.post(new InputEvent.MouseScrollEvent(x, y)));
        GLFW.glfwSetCursorEnterCallback(this.windowId,
                (window, entered) -> this.windowBus.post(new InputEvent.CursorInWindowEvent(entered)));
    }
    
    @Override
    public boolean isActive() {
        return this.isActive;
    }
    
    @Override
    public boolean isFullscreen() {
        return this.isFullscreen;
    }
    
    @Override
    public boolean isCloseRequested() {
        return GLFW.glfwWindowShouldClose(this.windowId);
    }
    
    @Override
    public int getWindowWidth() {
        return this.windowWidth;
    }
    
    @Override
    public int getWindowHeight() {
        return this.windowHeight;
    }
    
    public GLScreenBuffer getDefaultFrameBuffer() {
        return this.screenBuffer;
    }
    
    @Override
    public void setVSync(final boolean vsync) {
        GLFW.glfwSwapInterval(vsync ? 1 : 0);
    }
    
    @Override
    public void setVisible(final boolean visible) {
        if (visible) {
            GLFW.glfwShowWindow(this.windowId);
        } else {
            GLFW.glfwHideWindow(this.windowId);
        }
    }
    
    @Override
    public void dispose() {
        GLFW.glfwDestroyWindow(this.windowId);
    }
    
    @Override
    public void swapBuffers() {
        GLFW.glfwSwapBuffers(this.windowId);
    }
    
    @Override
    public long getID() {
        return this.windowId;
    }
    
    @Override
    public void setTitle(final String title) {
        GLFW.glfwSetWindowTitle(this.windowId, title);
    }
    
    @Override
    public void setWindowSize(final int width, final int height) {
        this.windowWidth = width;
        this.windowHeight = height;
        GLFW.glfwSetWindowSize(this.windowId, width, height);
    }
    
    @Override
    public void setFullscreen(final boolean b) {
        GLFW.glfwSetWindowMonitor(this.windowId, b ? GLFW.glfwGetPrimaryMonitor() : 0, 0, 0, this.windowWidth,
                this.windowHeight, GLFW.GLFW_DONT_CARE);
    }
    
    @Override
    public void setOpacity(float a) {
        GLFW.glfwSetWindowOpacity(this.windowId, a);
    }
    
}
