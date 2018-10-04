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

package de.omnikryptec.old.event.input;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.glfw.GLFW;

import de.omnikryptec.old.settings.KeySettings;

/**
 * JoystickHandler
 *
 * @author Panzer1119
 */
public class JoystickHandler implements InputHandler {
    
    private static final List<JoystickHandler> joystickHandlers = new CopyOnWriteArrayList<>();
    
    //private final JoystickHandler ME = this;
    private final int joystick;
    protected FloatBuffer dataAxes = null;
    protected ByteBuffer dataButtons = null;
    protected ByteBuffer dataHats = null;
    
    public JoystickHandler(int joystick) {
        this.joystick = joystick;
    }
    
    public static final String getName(int joystick) {
        return GLFW.glfwGetJoystickName(joystick);
    }
    
    public static final boolean isConnected(int joystick) {
        return getName(joystick) != null;
    }
    
    public static final List<Integer> getJoysticks() {
        final List<Integer> joysticks = new ArrayList<>();
        for (int i = GLFW.GLFW_JOYSTICK_1; i <= GLFW.GLFW_JOYSTICK_LAST; i++) {
            joysticks.add(i);
        }
        return joysticks;
    }
    
    public static final Map<Integer, String> getConnectedJoysticks() {
        final Map<Integer, String> joysticks = new HashMap<>();
        getJoysticks().stream().forEach((joystick) -> {
            final String name = getName(joystick);
            if (name != null) {
                joysticks.put(joystick, name);
            }
        });
        return joysticks;
    }
    
    protected static final synchronized void updateAll() {
        if (joystickHandlers.isEmpty()) {
            return;
        }
        for (JoystickHandler joystickHandler : joystickHandlers) {
            joystickHandler.update();
        }
    }
    
    protected static final synchronized void updateAll(double currentTime, KeySettings keySettings) {
        if (joystickHandlers.isEmpty()) {
            return;
        }
        for (JoystickHandler joystickHandler : joystickHandlers) {
            joystickHandler.updateKeySettings(currentTime, keySettings);
        }
    }
    
    public final synchronized JoystickHandler update() {
        if (!isConnected()) {
            return this;
        }
        dataAxes = GLFW.glfwGetJoystickAxes(joystick);
        dataButtons = GLFW.glfwGetJoystickButtons(joystick);
        dataHats = GLFW.glfwGetJoystickHats(joystick);
        return this;
    }
    
    public final JoystickHandler init() {
        if (!joystickHandlers.contains(this)) {
            joystickHandlers.add(this);
        }
        return this;
    }
    
    @Override
    public final JoystickHandler close() {
        joystickHandlers.remove(this);
        return this;
    }
    
    public final synchronized int getJoystick() {
        return joystick;
    }
    
    public final synchronized FloatBuffer getDataAxes() {
        return dataAxes;
    }
    
    public final synchronized ByteBuffer getDataButtons() {
        return dataButtons;
    }
    
    public final synchronized ByteBuffer getDataHats() {
        return dataHats;
    }
    
    public final synchronized String getName() {
        return getName(joystick);
    }
    
    public final synchronized boolean isConnected() {
        return isConnected(joystick);
    }
    
    @Override
    public final synchronized JoystickHandler preUpdate() {
        return this;
    }
    
    @Override
    public final synchronized JoystickHandler updateKeySettings(double currentTime, KeySettings keySettings) {
        return this;
    }
    
}
