/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opencl.CL;
import org.lwjgl.system.Configuration;

import de.codemakers.base.util.tough.ToughRunnable;
import de.omnikryptec.event.EventBus;
import de.omnikryptec.libapi.opencl.OpenCL;
import de.omnikryptec.util.Logger;
import de.omnikryptec.util.settings.Defaultable;
import de.omnikryptec.util.settings.Settings;

public final class LibAPIManager {
    
    public static final EventBus LIB_API_EVENT_BUS = new EventBus(false);
    private static final Collection<ToughRunnable> shutdownHooks = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(LibAPIManager.class);
    private static LibAPIManager instance;
    private GLFWAccessManager glfw;
    private OpenCL opencl;
    private static boolean debugFlag;
    
    private LibAPIManager() {
    }
    
    public static void init(@Nonnull Settings<LibSetting> settings) {
        if (isInitialized()) {
            throw new IllegalStateException("LibAPI is already initialized");
        }
        setConfiguration(settings);
        instance = new LibAPIManager();
        logger.info("Initialized LibAPI");
    }
    
    /**
     * Uses the settings to set library options. This method is only effective if no
     * library functions have been called yet.<br>
     *
     * @param settings the {@link Settings} to set the lib options from
     */
    private static void setConfiguration(@Nonnull Settings<LibSetting> settings) {
        if (isInitialized()) {
            logger.warn("Some settings may not have any effect because the LibAPI is initialized");
        }
        Logger.setMinLogType(settings.get(LibSetting.LOGGING_MIN));
        final boolean debug = settings.get(LibSetting.DEBUG);
        final boolean fastMath = settings.get(LibSetting.FAST_MATH);
        final boolean functionDebug = settings.get(LibSetting.DEBUG_FUNCTIONS);
        final boolean libLoadDebug = settings.get(LibSetting.DEBUG_LIBRARY_LOADING);
        if (fastMath) {
            System.setProperty("joml.fastmath", "true");
            logger.debug("Using joml.fastmath");
        }
        debugFlag = debug;
        Configuration.DEBUG.set(debug);
        Configuration.DEBUG_LOADER.set(debug && libLoadDebug);
        Configuration.DEBUG_FUNCTIONS.set(debug && functionDebug);
    }
    
    public static void shutdown() {
        if (isInitialized()) {
            logger.info("Running shutdown hooks...");
            for (ToughRunnable toughRunnable : shutdownHooks) {
                toughRunnable.run((throwable) -> {
                    logger.error(String.format("Exception in shutdown hook '%s': %s", toughRunnable, throwable));
                    throwable.printStackTrace();
                });
            }
            instance.terminateGlfw();
            instance.terminateOpenCL();
            instance = null;
            logger.info("Terminated LibAPI");
        }
    }
    
    public static void registerResourceShutdownHooks(ToughRunnable... toughRunnables) {
        shutdownHooks.addAll(Arrays.asList(toughRunnables));
    }
    
    public static boolean isInitialized() {
        return instance != null;
    }
    
    public static LibAPIManager instance() {
        return instance;
    }
    
    /**
     * Always prefer {@link Logger#debug(Object...)} over this if possible.
     * 
     */
    public static boolean debug() {
        return debugFlag;
    }
    
    public void initGlfw() {
        if (isGLFWinitialized()) {
            throw new IllegalStateException("GLFW is already initialized");
        }
        if (GLFW.glfwInit()) {
            GLFWErrorCallback.createThrow().set();
            glfw = new GLFWAccessManager();
            logger.info("Initialized GLFW");
        } else {
            glfw = null;
            throw new RuntimeException("Error while initializing GLFW");
        }
    }
    
    public void terminateGlfw() {
        if (isGLFWinitialized()) {
            GLFW.glfwTerminate();
            glfw = null;
            logger.info("Terminated GLFW");
        }
    }
    
    public GLFWAccessManager getGLFW() {
        return glfw;
    }
    
    public boolean isGLFWinitialized() {
        return glfw != null;
    }
    
    public void initOpenCL() {
        try {
            if (CL.getFunctionProvider() == null) {
                CL.create();
            }
            opencl = new OpenCL();
            logger.info("Initialized OpenCL");
        } catch (Exception ex) {
            opencl = null;
            throw new RuntimeException(ex);
        }
    }
    
    public void terminateOpenCL() {
        if (isOpenCLinitialized()) {
            opencl.shutdown();
            opencl = null;
            logger.info("Terminated OpenCL");
        }
    }
    
    public boolean isOpenCLinitialized() {
        return opencl != null;
    }
    
    public OpenCL getOpenCL() {
        return opencl;
    }
    
    public enum LibSetting implements Defaultable {
        /**
         * Enables debug mode of the OmniKryptec-Engine and LWJGL. This might do
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
        FAST_MATH(true),
        /**
         * The minimum {@link Logger.LogType} that will be logged. <br>
         * <br>
         * The default value is the {@link Logger}'s default value.
         *
         * @see de.omnikryptec.util.Logger
         */
        LOGGING_MIN(null), DEBUG_LIBRARY_LOADING(false);
        
        private final Object defaultSetting;
        
        LibSetting(Object defaultSetting) {
            this.defaultSetting = defaultSetting;
        }
        
        @Override
        public <T> T getDefault() {
            return (T) this.defaultSetting;
        }
    }
    
}
