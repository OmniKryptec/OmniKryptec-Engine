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

package de.omnikryptec.libapi.exposed.input;

import de.codemakers.base.logger.Logger;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.util.settings.KeySettings;
import org.lwjgl.glfw.GLFW;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class JoystickHandler implements InputHandler {
    
    private static final List<JoystickHandler> joystickHandlers = new CopyOnWriteArrayList<>();
    
    static {
        // Runtime.getRuntime().addShutdownHook(new Thread(() -> closeAll(),
        // JoystickHandler.class.getName() + "-Shutdown-Thread"));
        LibAPIManager.registerResourceShutdownHooks(() -> closeAll());
    }
    
    private final int joystick;
    private FloatBuffer dataAxes = null;
    private ByteBuffer dataButtons = null;
    private ByteBuffer dataHats = null;
    
    public JoystickHandler(final int joystick) {
        this.joystick = joystick;
        init();
    }
    
    public static synchronized String getName(final int joystick) {
        return GLFW.glfwGetJoystickName(joystick);
    }
    
    public static synchronized String getGUID(final int joystick) {
        return GLFW.glfwGetJoystickGUID(joystick);
    }
    
    public static synchronized boolean isConnected(final int joystick) {
        return getName(joystick) != null;
    }
    
    public static List<Integer> getJoysticks() {
        final List<Integer> joysticks = new ArrayList<>();
        for (int i = GLFW.GLFW_JOYSTICK_1; i <= GLFW.GLFW_JOYSTICK_LAST; i++) {
            joysticks.add(i);
        }
        return joysticks;
    }
    
    public static synchronized Map<Integer, String> getConnectedJoysticks() {
        final Map<Integer, String> joysticks = new HashMap<>();
        getJoysticks().forEach((joystick) -> {
            final String name = getName(joystick);
            if (name != null) {
                joysticks.put(joystick, name);
            }
        });
        return joysticks;
    }
    
    public static synchronized void preUpdateAll(final double currentTime, final KeySettings keySettings) {
        if (joystickHandlers.isEmpty()) {
            return;
        }
        for (final JoystickHandler joystickHandler : joystickHandlers) {
            try {
                joystickHandler.preUpdate(currentTime, keySettings);
            } catch (final Exception ex) {
                Logger.handleError(ex);
            }
        }
    }
    
    public static synchronized void updateAll(final double currentTime, final KeySettings keySettings) {
        if (joystickHandlers.isEmpty()) {
            return;
        }
        for (final JoystickHandler joystickHandler : joystickHandlers) {
            try {
                joystickHandler.update(currentTime, keySettings);
            } catch (final Exception ex) {
                Logger.handleError(ex);
            }
        }
    }
    
    public static synchronized void postUpdateAll(final double currentTime, final KeySettings keySettings) {
        if (joystickHandlers.isEmpty()) {
            return;
        }
        for (final JoystickHandler joystickHandler : joystickHandlers) {
            try {
                joystickHandler.postUpdate(currentTime, keySettings);
            } catch (final Exception ex) {
                Logger.handleError(ex);
            }
        }
    }
    
    public static synchronized void closeAll() {
        if (joystickHandlers.isEmpty()) {
            return;
        }
        for (final JoystickHandler joystickHandler : joystickHandlers) {
            try {
                joystickHandler.close();
            } catch (final Exception ex) {
                Logger.handleError(ex);
            }
        }
    }
    
    public static List<JoystickHandler> getJoystickHandlers() {
        return joystickHandlers;
    }
    
    @Override
    public synchronized InputHandler init() {
        if (!joystickHandlers.contains(this)) {
            joystickHandlers.add(this);
        }
        return this;
    }
    
    @Override
    public synchronized InputHandler preUpdate(final double currentTime, final KeySettings keySettings) {
        return this;
    }
    
    @Override
    public synchronized InputHandler update(final double currentTime, final KeySettings keySettings) {
        /*
         * if (!isConnected()) { return this; }
         */
        synchronized (this.dataAxes) {
            this.dataAxes = GLFW.glfwGetJoystickAxes(this.joystick);
        }
        synchronized (this.dataButtons) {
            this.dataButtons = GLFW.glfwGetJoystickButtons(this.joystick);
        }
        synchronized (this.dataHats) {
            this.dataHats = GLFW.glfwGetJoystickHats(this.joystick);
        }
        return this;
    }
    
    @Override
    public synchronized InputHandler postUpdate(final double currentTime, final KeySettings keySettings) {
        return this;
    }
    
    @Override
    public synchronized InputHandler close() {
        joystickHandlers.remove(this);
        return this;
    }
    
    public synchronized int getJoystick() {
        return this.joystick;
    }
    
    public synchronized FloatBuffer getDataAxes() {
        return this.dataAxes;
    }
    
    public synchronized ByteBuffer getDataButtons() {
        return this.dataButtons;
    }
    
    public synchronized ByteBuffer getDataHats() {
        return this.dataHats;
    }
    
    public synchronized String getName() {
        return getName(this.joystick);
    }
    
    public synchronized String getGUID() {
        return getGUID(this.joystick);
    }
    
    public synchronized boolean isConnected() {
        return isConnected(this.joystick);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !getClass().isAssignableFrom(o.getClass())) {
            return false;
        }
        final JoystickHandler that = (JoystickHandler) o;
        return this.joystick == that.joystick;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.joystick);
    }
    
}
