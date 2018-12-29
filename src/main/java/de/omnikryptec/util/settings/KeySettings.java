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

package de.omnikryptec.util.settings;

import java.util.Collection;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.ObjectArrays;

import de.omnikryptec.util.settings.keys.IKey;
import de.omnikryptec.util.settings.keys.Key;
import de.omnikryptec.util.settings.keys.KeyContainer;
import de.omnikryptec.util.settings.keys.KeyGroup;

public class KeySettings extends KeyContainer {
    
    public static final byte KEY_UNKNOWN = Byte.MIN_VALUE;
    public static final byte KEY_NOTHING = -1;
    public static final byte KEY_RELEASED = GLFW.GLFW_RELEASE;
    public static final byte KEY_PRESSED = GLFW.GLFW_PRESS;
    public static final byte KEY_REPEATED = GLFW.GLFW_REPEAT;
    
    public static final Key STANDARD_MOUSE_BUTTON_LEFT = new Key("mouseButtonLeft", GLFW.GLFW_MOUSE_BUTTON_LEFT, false);
    public static final Key STANDARD_MOUSE_BUTTON_RIGHT = new Key("mouseButtonRight", GLFW.GLFW_MOUSE_BUTTON_RIGHT,
            false);
    public static final Key STANDARD_MOUSE_BUTTON_MIDDLE = new Key("mouseButtonMiddle", GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
            false);
    public static final Key STANDARD_KEY_MOVE_FORWARD = new Key("moveForward", GLFW.GLFW_KEY_W);
    public static final Key STANDARD_KEY_MOVE_BACKWARD = new Key("moveBackward", GLFW.GLFW_KEY_S);
    public static final Key STANDARD_KEY_MOVE_RIGHT = new Key("moveRight", GLFW.GLFW_KEY_D);
    public static final Key STANDARD_KEY_MOVE_LEFT = new Key("moveLeft", GLFW.GLFW_KEY_A);
    public static final Key STANDARD_KEY_MOVE_UP = new Key("moveUp", GLFW.GLFW_KEY_SPACE);
    public static final Key STANDARD_KEY_MOVE_DOWN = new Key("moveDown", GLFW.GLFW_KEY_LEFT_SHIFT);
    public static final Key STANDARD_KEY_TURN_YAW_RIGHT = new Key("turnYawRight", GLFW.GLFW_KEY_RIGHT);
    public static final Key STANDARD_KEY_TURN_YAW_LEFT = new Key("turnYawLeft", GLFW.GLFW_KEY_LEFT);
    public static final Key STANDARD_KEY_TURN_PITCH_UP = new Key("turnPitchUp", GLFW.GLFW_KEY_UP);
    public static final Key STANDARD_KEY_TURN_PITCH_DOWN = new Key("turnPitchDown", GLFW.GLFW_KEY_DOWN);
    public static final Key STANDARD_KEY_TURN_ROLL_RIGHT = new Key("turnRollRight", GLFW.GLFW_KEY_E);
    public static final Key STANDARD_KEY_TURN_ROLL_LEFT = new Key("turnRollLeft", GLFW.GLFW_KEY_Q);
    
    public static final Key[] STANDARD_MOUSE_BUTTONS = new Key[] { STANDARD_MOUSE_BUTTON_LEFT,
            STANDARD_MOUSE_BUTTON_RIGHT, STANDARD_MOUSE_BUTTON_MIDDLE };
    public static final Key[] STANDARD_KEYBOARD_KEYS = new Key[] { STANDARD_KEY_MOVE_FORWARD,
            STANDARD_KEY_MOVE_BACKWARD, STANDARD_KEY_MOVE_RIGHT, STANDARD_KEY_MOVE_LEFT, STANDARD_KEY_MOVE_UP,
            STANDARD_KEY_MOVE_DOWN, STANDARD_KEY_TURN_YAW_RIGHT, STANDARD_KEY_TURN_YAW_LEFT, STANDARD_KEY_TURN_PITCH_UP,
            STANDARD_KEY_TURN_PITCH_DOWN, STANDARD_KEY_TURN_ROLL_RIGHT, STANDARD_KEY_TURN_ROLL_LEFT };
    public static final Key[] STANDARD_KEYS = ObjectArrays.concat(STANDARD_MOUSE_BUTTONS, STANDARD_KEYBOARD_KEYS,
            Key.class);
    
    public KeySettings() {
        this(null);
    }
    
    public KeySettings(final Collection<IKey> keys) {
        if (keys != null) {
            addIKeys(keys);
        }
    }
    
    public static void updateKeys(final Collection<IKey> keys, final double currentTime, final int keyCode,
            final boolean isKeyboardKey) {
        for (final IKey key : keys) {
            if (key instanceof Key) {
                final Key key_ = (Key) key;
                if (key_.getKey() == keyCode && key_.isKeyboardKey() == isKeyboardKey) {
                    key_.setLastChange(currentTime);
                }
            } else if (key instanceof KeyGroup) {
                updateKeys(((KeyGroup) key).getIKeys(), currentTime, keyCode, isKeyboardKey);
            }
        }
    }
    
    public void updateKeys(final double currentTime, final int keyCode, final boolean isKeyboardKey) {
        updateKeys(getIKeys(), currentTime, keyCode, isKeyboardKey);
    }
    
    public boolean isPressed(final String name) {
        final IKey key = getIKey(name);
        return key == null ? false : key.isPressed();
    }
    
    public boolean isLongPressed(final String name, final double minTime, final double maxTime) {
        final IKey key = getIKey(name);
        return key == null ? false : key.isLongPressed(minTime, maxTime);
    }
    
}
