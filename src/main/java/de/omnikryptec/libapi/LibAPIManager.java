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

package de.omnikryptec.libapi;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.Configuration;

import de.codemakers.base.util.tough.ToughRunnable;
import de.omnikryptec.core.EngineLoader.LoaderSetting;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.opengl.OpenGLRendererAPI;
import de.omnikryptec.util.settings.Defaultable;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;

public final class LibAPIManager {

    public static enum LibSetting implements Defaultable {
        /**
         * Enables debug mode of the Omnikryptec-Engine and LWJGL. This might do
         * expensive checks, performance-wise.<br>
         * <br>
         * The default value is <code>false</code>.
         *
         * @see org.lwjgl.system.Configuration#DEBUG
         * @see org.lwjgl.system.Configuration#DEBUG_LOADER
         */
        DEBUG(false),
        /**
         * When enabled, lwjgl's capabilities classes will print an error message when
         * they fail to retrieve a function pointer. <br>
         * Requires {@link #DEBUG} to be enabled. <br>
         * <br>
         * The default value is <code>false</code>.
         *
         * @see org.lwjgl.system.Configuration#DEBUG_FUNCTIONS
         */
        DEBUG_FUNCTIONS(false),
        /**
         * Enables joml's fastmath. <br>
         * <br>
         * The default value is <code>true</code>.
         *
         * @see org.joml.Math
         */
        FASTMATH(true);

        private final Object defaultSetting;

        LibSetting(final Object def) {
            this.defaultSetting = def;
        }

        @Override
        public <T> T getDefault() {
            return (T) this.defaultSetting;
        }
    }

    private static final Collection<ToughRunnable> shutdownHooks = new ArrayList<>();
    private static LibAPIManager instance;

    private static boolean debug = (boolean) LibSetting.DEBUG.getDefault();
    private RenderAPI renderApi;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown(), "LibAPI-Shutdown-Hooks"));
    }

    private LibAPIManager() {
    }

    @Deprecated
    public static void init() {
        init(new Settings<>());
    }

    public static void init(@Nonnull final Settings<LibSetting> settings) {
        if (isInitialized()) {
            throw new IllegalStateException("Already initialized");
        }
        setConfiguration(settings);
        if (GLFW.glfwInit()) {
            GLFWErrorCallback.createThrow().set();
            instance = new LibAPIManager();
            System.out.println("Initialized LibAPI");
        } else {
            instance = null;
            throw new RuntimeException("Error while initializing LibAPI");
        }
    }

    /**
     * Uses the settings to set library options. This method is only effective if no
     * library functions have been called yet.<br>
     *
     * @param settings the {@link Settings} to set the lib options from
     */
    private static void setConfiguration(@Nonnull final Settings<LibSetting> settings) {
        if (isInitialized()) {
            // TODO Logger.WARNING(might not get set)
            throw new IllegalStateException();
        }
        debug = settings.get(LibSetting.DEBUG);
        final boolean fastmath = settings.get(LibSetting.FASTMATH);
        final boolean functionDebug = settings.get(LibSetting.DEBUG_FUNCTIONS);
        if (fastmath) {
            System.setProperty("joml.fastmath", "true");
        }
        Configuration.DEBUG.set(debug);
        Configuration.DEBUG_LOADER.set(debug);
        Configuration.DEBUG_FUNCTIONS.set(debug && functionDebug);
    }

    public static void shutdown() {
        for (final ToughRunnable r : shutdownHooks) {
            try {
                r.run();
            } catch (final Exception e) {
                System.err.println("Exception in shutdown hook '" + r + "': " + e);
                e.printStackTrace();
            }
        }
        if (isInitialized()) {
            GLFW.glfwTerminate();
            instance = null;
            System.out.println("Terminated LibAPI");
        }
    }

    public static void registerResourceShutdownHooks(final ToughRunnable... runnables) {
        shutdownHooks.addAll(Arrays.asList(runnables));
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    public boolean debug() {
        return debug;
    }

    public static LibAPIManager active() {
        return instance;
    }

    public void setRenderer(Class<? extends RenderAPI> apiclazz, Settings<IntegerKey> apisettings) {
        if (isRendererSet()) {
            throw new IllegalStateException("Renderer is already set!");
        }
        try {
            Constructor<? extends RenderAPI> rApiConstructor = apiclazz.getConstructor(apisettings.getClass());
            if (!rApiConstructor.isAccessible()) {
                rApiConstructor.setAccessible(true);
            }
            renderApi = rApiConstructor.newInstance(apisettings);
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException("Invalid RendererAPI: Missing constructor", ex);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isRendererSet() {
        return renderApi != null;
    }

    public RenderAPI getRenderer() {
        return renderApi;
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
