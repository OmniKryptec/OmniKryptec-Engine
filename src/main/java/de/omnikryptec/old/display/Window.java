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

package de.omnikryptec.old.display;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.nio.IntBuffer;

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
        if(info.getMajorVersion()>3||(info.getMajorVersion()>2&&info.getMinorVersion()>1)) {
	        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, info.getMajorVersion());
	        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, info.getMinorVersion());
	        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        }
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
        onResize(width, height);
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
