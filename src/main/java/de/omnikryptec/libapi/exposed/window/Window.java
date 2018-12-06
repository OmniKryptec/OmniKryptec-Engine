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

import de.omnikryptec.util.Util;

public abstract class Window<T extends WindowInfo<?>> {
    protected final long windowId;
    private final GLFWFramebufferSizeCallback framebufferSizeCallback;
    private boolean resized = false;
    private int width, height, fwidth, fheight;
    private boolean isfullscreen = false;
    private boolean active = false;

    protected Window(final T info) {
        Util.ensureNonNull(info, "Window info must not be null!");
        this.width = info.getWidth();
        this.height = info.getHeight();
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, info.isResizeable() ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        setAdditionalGlfwWindowHints(info);
        if (info.isFullscreen()) {
            final GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            this.width = vidMode.width();
            this.height = vidMode.height();
            this.isfullscreen = true;
        }
        this.windowId = GLFW.glfwCreateWindow(this.width, this.height, info.getName(),
                info.isFullscreen() ? GLFW.glfwGetPrimaryMonitor() : 0, 0);
        if (this.windowId == 0) {
            throw new RuntimeException("Failed to create window");
        }
        if (info.isLockAspectRatio() && info.isResizeable()) {
            GLFW.glfwSetWindowAspectRatio(this.windowId, this.width, this.height);
        }
        GLFW.glfwSetFramebufferSizeCallback(this.windowId,
                (this.framebufferSizeCallback = new GLFWFramebufferSizeCallback() {
                    @Override
                    public void invoke(final long window, final int width, final int height) {
                        onResize(width, height);
                    }
                }));
        final IntBuffer framebufferWidth = BufferUtils.createIntBuffer(1),
                framebufferHeight = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetFramebufferSize(this.windowId, framebufferWidth, framebufferHeight);
        this.fwidth = framebufferWidth.get();
        this.fheight = framebufferHeight.get();
    }

    protected abstract void setAdditionalGlfwWindowHints(T info);

    protected abstract void swap();

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

    protected GLFWFramebufferSizeCallback getDisplaySizeCallback() {
        return this.framebufferSizeCallback;
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
