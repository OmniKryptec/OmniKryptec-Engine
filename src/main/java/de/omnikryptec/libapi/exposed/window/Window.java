/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.libapi.exposed.window;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowFocusCallbackI;
import org.lwjgl.glfw.GLFWWindowIconifyCallbackI;
import org.lwjgl.glfw.GLFWWindowMaximizeCallbackI;

import de.omnikryptec.event.EventBus;
import de.omnikryptec.event.ReadableEventBus;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.settings.Defaultable;
import de.omnikryptec.util.settings.Settings;

public abstract class Window {
    
    public static enum WindowSetting implements Defaultable {
        Width(800), Height(600), Fullscreen(false), Name("Display"), Resizeable(true), LockAspectRatio(false),
        /**
         * @see Window#setVSync(boolean)
         * @see de.omnikryptec.core.scene.UpdateController#setSyncUpdatesPerSecond(int)
         * @see de.omnikryptec.core.scene.UpdateController#setAsyncUpdatesPerSecond(int)
         */
        VSync(true);
        
        private final Object def;
        
        private WindowSetting(final Object def) {
            this.def = def;
        }
        
        @Override
        public <T> T getDefault() {
            return (T) this.def;
        }
        
    }
    
    protected final long windowId;
    private boolean resized = false;
    private int width, height, fwidth, fheight;
    private boolean isfullscreen = false;
    private boolean active = false;
    
    protected Window(final Settings<WindowSetting> info, final Object... hints) {
        Util.ensureNonNull(info, "Window settings must not be null!");
        this.width = info.get(WindowSetting.Width);
        this.height = info.get(WindowSetting.Height);
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE,
                (boolean) info.get(WindowSetting.Resizeable) ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        setAdditionalGlfwWindowHints(hints);
        if ((boolean) info.get(WindowSetting.Fullscreen)) {
            final GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            this.width = vidMode.width();
            this.height = vidMode.height();
            this.isfullscreen = true;
        }
        this.windowId = GLFW.glfwCreateWindow(this.width, this.height, (String) info.get(WindowSetting.Name),
                (boolean) info.get(WindowSetting.Fullscreen) ? GLFW.glfwGetPrimaryMonitor() : 0, 0);
        if (this.windowId == 0) {
            throw new RuntimeException("Failed to create window");
        }
        if ((boolean) info.get(WindowSetting.LockAspectRatio) && (boolean) info.get(WindowSetting.Resizeable)) {
            GLFW.glfwSetWindowAspectRatio(this.windowId, this.width, this.height);
        }
        registerCallbacks();
        final IntBuffer framebufferWidth = BufferUtils.createIntBuffer(1),
                framebufferHeight = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetFramebufferSize(this.windowId, framebufferWidth, framebufferHeight);
        this.fwidth = framebufferWidth.get();
        this.fheight = framebufferHeight.get();
    }
    
    protected abstract void setAdditionalGlfwWindowHints(Object... hints);
    
    protected abstract void swap();
    
    public abstract void setVSync(boolean vsync);
    
    public long getWindowID() {
        return this.windowId;
    }
    
    public void setVisible(final boolean b) {
        if (b) {
            GLFW.glfwShowWindow(this.windowId);
        } else {
            GLFW.glfwHideWindow(this.windowId);
        }
    }
    
    public void dispose() {
        GLFW.glfwDestroyWindow(this.windowId);
    }
    
    public void swapBuffers() {
        this.active = GLFW.glfwGetWindowAttrib(this.windowId, GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE;
        this.resized = false;
        swap();
    }
    
    public boolean shouldBeFullscreen() {
        return this.isfullscreen;
    }
    
    public boolean wasResized() {
        return this.resized;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public boolean isCloseRequested() {
        return GLFW.glfwWindowShouldClose(this.windowId);
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getBufferWidth() {
        return this.fwidth;
    }
    
    public int getBufferHeight() {
        return this.fheight;
    }
    
    private void registerCallbacks() {
        EventBus windowBus = LibAPIManager.LIBAPI_EVENTBUS;
        GLFW.glfwSetFramebufferSizeCallback(this.windowId, (new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(final long window, final int width, final int height) {
                onResize(width, height);
                windowBus.post(new WindowEvent.WindowResized(width, height));
            }
        }));
        GLFW.glfwSetWindowFocusCallback(windowId, new GLFWWindowFocusCallbackI() {
            
            @Override
            public void invoke(long window, boolean focused) {
                windowBus.post(new WindowEvent.WindowFocused(focused));
            }
        });
        GLFW.glfwSetWindowIconifyCallback(windowId, new GLFWWindowIconifyCallbackI() {
            
            @Override
            public void invoke(long window, boolean iconified) {
                windowBus.post(new WindowEvent.WindowIconified(iconified));
            }
        });
        GLFW.glfwSetWindowMaximizeCallback(windowId, new GLFWWindowMaximizeCallbackI() {
            
            @Override
            public void invoke(long window, boolean maximized) {
                windowBus.post(new WindowEvent.WindowMaximized(maximized));
            }
        });
    }
    
    protected void onResize(final int w, final int h) {
        this.width = w;
        this.height = h;
        this.resized = true;
        final IntBuffer framebufferWidth = BufferUtils.createIntBuffer(1),
                framebufferHeight = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetFramebufferSize(this.windowId, framebufferWidth, framebufferHeight);
        this.fwidth = framebufferWidth.get();
        this.fheight = framebufferHeight.get();
    }
}
