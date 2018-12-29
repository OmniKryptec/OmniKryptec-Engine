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

package de.omnikryptec.libapi.opengl;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;

public class OpenGLWindow extends Window {

    public OpenGLWindow(final Settings<WindowSetting> info, final Settings<IntegerKey> apisettings) {
        super(info, apisettings);
        GLFW.glfwMakeContextCurrent(getWindowID());
        GL.createCapabilities();
        setVSync(info.get(WindowSetting.VSync));
    }

    @Override
    protected void setAdditionalGlfwWindowHints(final Object... hints) {
        final Settings<IntegerKey> apisettings = (Settings<IntegerKey>) hints[0];
        final int mav = apisettings.get(OpenGLRenderAPI.MAJOR_VERSION);
        final int miv = apisettings.get(OpenGLRenderAPI.MINOR_VERSION);
        if (mav > 3 || (mav > 2 && miv > 1)) {
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, mav);
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, miv);
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        }
    }

    @Override
    protected void swap() {
        GLFW.glfwSwapBuffers(this.windowId);
    }

    @Override
    public void setVSync(final boolean vsync) {
        GLFW.glfwSwapInterval(vsync ? 1 : 0);
    }

}
