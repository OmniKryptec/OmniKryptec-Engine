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

package de.omnikryptec.libapi.glfw;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public final class LibAPIManager {

    private static final Collection<Runnable> shutdownHooks = new ArrayList<>();
    private static LibAPIManager instance;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown(), "LibAPI-Shutdown-Hooks"));
    }

    private LibAPIManager() {
    }

    public static void init() {
        if (isInitialized()) {
            throw new IllegalStateException("Already initialized");
        }
        if (GLFW.glfwInit()) {
            GLFWErrorCallback.createThrow().set();
            instance = new LibAPIManager();
            System.out.println("Initialized LibAPI");
        } else {
            instance = null;
            throw new RuntimeException("Error while initializing LibAPI");
        }
    }

    public static void shutdown() {
        for (Runnable r : shutdownHooks) {
            try {
                r.run();
            } catch (Exception e) {
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

    public static void registerResourceShutdownHooks(Runnable... runnables) {
        shutdownHooks.addAll(Arrays.asList(runnables));
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    public static LibAPIManager active() {
        return instance;
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
