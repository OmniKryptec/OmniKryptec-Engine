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

package de.omnikryptec.libapi.exposed;

import java.lang.reflect.Constructor;

import org.lwjgl.glfw.GLFW;

import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.util.Logger;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;

public class GLFWAccessManager {
    
    private RenderAPI renderApi;
    private final Logger logger = Logger.getLogger(getClass());
    
    GLFWAccessManager() {
    }
    
    public void setRenderer(final Class<? extends RenderAPI> clazz, final Settings<WindowSetting> windowSettings,
            final Settings<IntegerKey> apiSettings) {
        if (isRendererSet()) {
            throw new IllegalStateException("Renderer is already set!");
        }
        try {
            final Constructor<? extends RenderAPI> renderApiConstructor = clazz
                    .getConstructor(windowSettings.getClass(), apiSettings.getClass());
            renderApiConstructor.setAccessible(true);
            this.renderApi = renderApiConstructor.newInstance(windowSettings, apiSettings);
            this.logger.info("Set the RenderAPI to " + clazz.getSimpleName());
        } catch (final NoSuchMethodException ex) {
            throw new IllegalArgumentException("Invalid RendererAPI: Missing constructor", ex);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean isRendererSet() {
        return this.renderApi != null;
    }
    
    public RenderAPI getRenderAPI() {
        return this.renderApi;
    }
    
    public void pollEvents() {
        GLFW.glfwPollEvents();
    }
    
    /**
     * Returns the value of the GLFW timer. The timer measures time elapsed since
     * GLFW was initialized.
     * <p>
     * The resolution of the timer is system dependent, but is usually on the order
     * of a few micro- or nanoseconds. It uses the highest-resolution monotonictime
     * source on each supported platform.
     *
     * @return the current value, in seconds, or zero if an error occurred
     */
    public double getTime() {
        return GLFW.glfwGetTime();
    }
    
}
