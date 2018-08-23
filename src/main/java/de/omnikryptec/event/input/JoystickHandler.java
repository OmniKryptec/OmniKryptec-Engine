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

package de.omnikryptec.event.input;

import de.omnikryptec.settings.KeySettings;
import org.lwjgl.glfw.GLFW;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * JoystickHandler
 *
 * @author Panzer1119
 */
public class JoystickHandler implements InputHandler {

    private static final ArrayList<JoystickHandler> joystickHandlers = new ArrayList<>();

    //private final JoystickHandler ME = this;
    private final int joystick;
    protected FloatBuffer dataAxes = null;
    protected ByteBuffer dataButtons = null;
    protected ByteBuffer dataHats = null;

    public JoystickHandler(int joystick) {
        this.joystick = joystick;
    }

    public final JoystickHandler update() {
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

    public final int getJoystick() {
        return joystick;
    }

    public final FloatBuffer getDataAxes() {
        return dataAxes;
    }

    public final ByteBuffer getDataButtons() {
        return dataButtons;
    }

    public final ByteBuffer getDataHats() {
        return dataHats;
    }

    public final String getName() {
        return getName(joystick);
    }

    public final boolean isConnected() {
        return isConnected(joystick);
    }

    public static final String getName(int joystick) {
        return GLFW.glfwGetJoystickName(joystick);
    }

    public static final boolean isConnected(int joystick) {
        return getName(joystick) != null;
    }

    public static final ArrayList<Integer> getJoysticks() {
        final ArrayList<Integer> joysticks = new ArrayList<>();
        for (int i = GLFW.GLFW_JOYSTICK_1; i <= GLFW.GLFW_JOYSTICK_LAST; i++) {
            joysticks.add(i);
        }
        return joysticks;
    }

    public static final HashMap<Integer, String> getConnectedJoysticks() {
        final HashMap<Integer, String> joysticks = new HashMap<>();
        getJoysticks().stream().forEach((joystick) -> {
            final String name = getName(joystick);
            if (name != null) {
                joysticks.put(joystick, name);
            }
        });
        return joysticks;
    }

    protected static final void updateAll() {
        if(joystickHandlers.isEmpty()) {
            return;
        }
        for(JoystickHandler joystickHandler : joystickHandlers) {
            joystickHandler.update();
        }
    }
    
    protected static final void updateAll(double currentTime, KeySettings keySettings) {
        if(joystickHandlers.isEmpty()) {
            return;
        }
        for(JoystickHandler joystickHandler : joystickHandlers) {
            joystickHandler.updateKeySettings(currentTime, keySettings);
        }
    }

    @Override
    public final JoystickHandler preUpdate() {
        return this;
    }
    
    @Override
    public final JoystickHandler updateKeySettings(double currentTime, KeySettings keySettings) {
        return this;
    }

}
