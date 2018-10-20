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

package de.omnikryptec.graphics.display;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;

import de.omnikryptec.util.Util;

abstract class Window {
	private GLFWFramebufferSizeCallback framebufferSizeCallback;
	private boolean resized = false;
	private final long windowId;
	private int width, height, fwidth, fheight;
	private boolean isfullscreen = false;
	private boolean active = false;

	Window(WindowInfo info) {
		this.width = info.getWidth();
		this.height = info.getHeight();
		Util.ensureNonNull(info.getName(), "Window label must not be null!");
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, info.isResizeable() ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		setAdditionalGlfwWindowHints();
		if (info.isFullscreen()) {
			GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
			width = vidMode.width();
			height = vidMode.height();
			isfullscreen = true;
		}
		windowId = GLFW.glfwCreateWindow(width, height, info.getName(), info.isFullscreen() ? GLFW.glfwGetPrimaryMonitor() : 0, 0);
		if (windowId == 0) {
			throw new RuntimeException("Failed to create window");
		}
		if (info.isLockAspectRatio() && info.isResizeable()) {
			GLFW.glfwSetWindowAspectRatio(windowId, width, height);
		}
		GLFW.glfwSetFramebufferSizeCallback(windowId, (framebufferSizeCallback = new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				onResize(width, height);
			}
		}));
		IntBuffer framebufferWidth = BufferUtils.createIntBuffer(1), framebufferHeight = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetFramebufferSize(windowId, framebufferWidth, framebufferHeight);
		fwidth = framebufferWidth.get();
		fheight = framebufferHeight.get();
		onInitFinish();
	}

	protected abstract void setAdditionalGlfwWindowHints();

	protected abstract void onInitFinish();

	protected long getWindowID() {
		return windowId;
	}

	protected void show() {
		GLFW.glfwShowWindow(windowId);
	}

	protected void dispose() {
		GLFW.glfwDestroyWindow(windowId);
	}

	protected void swapBuffers() {
		active = GLFW.glfwGetWindowAttrib(windowId, GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE;
		resized = false;
		GLFW.glfwSwapBuffers(windowId);
	}

	public boolean shouldBeFullscreen() {
		return isfullscreen;
	}

	public boolean wasResized() {
		return resized;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isCloseRequested() {
		return GLFW.glfwWindowShouldClose(windowId);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getBufferWidth() {
		return fwidth;
	}

	public int getBufferHeight() {
		return fheight;
	}

	protected GLFWFramebufferSizeCallback getDisplaySizeCallback() {
		return framebufferSizeCallback;
	}

	protected void onResize(int w, int h) {
		width = w;
		height = h;
		resized = true;
		IntBuffer framebufferWidth = BufferUtils.createIntBuffer(1), framebufferHeight = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetFramebufferSize(windowId, framebufferWidth, framebufferHeight);
		fwidth = framebufferWidth.get();
		fheight = framebufferHeight.get();
	}
}
