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

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

public class OpenGLWindow extends Window<OpenGLWindowInfo> {

    public OpenGLWindow(OpenGLWindowInfo info) {
        super(info);
        GLFW.glfwMakeContextCurrent(getWindowID());
        GL.createCapabilities();
        GLFW.glfwSwapInterval(info.isVsync() ? 1 : 0);
    }

    @Override
    protected void setAdditionalGlfwWindowHints(OpenGLWindowInfo info) {
        int mav = info.getMajVersion();
        int miv = info.getMinVersion();
        if (mav > 3 || (mav > 2 && miv > 1)) {
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, mav);
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, miv);
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        }
    }

    @Override
    protected void swap() {
        GLFW.glfwSwapBuffers(windowId);
    }

}
