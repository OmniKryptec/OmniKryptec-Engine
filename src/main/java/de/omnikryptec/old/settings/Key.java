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

package de.omnikryptec.old.settings;

import org.jdom2.Element;
import org.lwjgl.glfw.GLFW;

import de.omnikryptec.old.event.input.InputManager;
import de.omnikryptec.old.main.OmniKryptecEngine;

/**
 * Key
 *
 * @author Panzer1119
 */
public class Key implements IKey {

    /**
     * Default Key which gets returned instead of null
     */
    public static final Key DEFAULT_NULL_KEY = new Key("DEFAULT_NULL_KEY", -1, true);

    private final String name;
    private int key = -1;
    private boolean isKeyboardKey = true;
    private double lastChange = 0.0F;

    /**
     * Constructs a key
     *
     * @param name String Name
     * @param key Integer Key
     * @param isKeyboardKey Boolean If the key is a keyboard key
     */
    public Key(String name, int key, boolean isKeyboardKey) {
        this.name = name;
        this.key = key;
        this.isKeyboardKey = isKeyboardKey;
    }

    @Override
    public final String getName() {
        return name;
    }

    /**
     * Sets the key
     *
     * @param key Integer Keyboard/Mouse key reference
     * @return Key A reference to this Key
     */
    public final Key setKey(int key) {
        this.key = key;
        return this;
    }

    /**
     * Returns the key
     *
     * @return Integer Keyboard/Mouse key reference
     */
    public final int getKey() {
        return key;
    }

    /**
     * Sets if this Key is a keyboard key
     *
     * @param isKeyboardKey Boolean If the key is a keyboard key
     * @return Key A reference to this Key
     */
    public final Key setIsKeyboardKey(boolean isKeyboardKey) {
        this.isKeyboardKey = isKeyboardKey;
        return this;
    }

    /**
     * Returns if the key is a keyboard key
     *
     * @return <tt>true</tt> if the key is a keyboard key
     */
    public final boolean isKeyboardKey() {
        return isKeyboardKey;
    }

    /**
     * Returns the last change of this Key
     *
     * @return Float Last change
     */
    public final double getLastChange() {
        return lastChange;
    }

    /**
     * Sets the last change of this Key
     *
     * @param lastChange Float Last change
     * @return Key A reference to this Key
     */
    public final Key setLastChange(double lastChange) {
        this.lastChange = lastChange;
        return this;
    }

    @Override
    public final boolean isPressed() {
        if (isKeyboardKey) {
            return InputManager.isKeyboardKeyPressed(key);
        } else {
            return InputManager.isMouseButtonPressed(key);
        }
    }

    @Override
    public boolean isLongPressed(double minTime, double maxTime) {
        final double currentTime = OmniKryptecEngine.instance().getDisplayManager().getCurrentTime();
        final double pressedTime = (currentTime - lastChange);
        final boolean isLongPressed = isPressed() && (pressedTime >= minTime && pressedTime <= maxTime);
        if (isLongPressed) {
            lastChange = currentTime;
        }
        return isLongPressed;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Key) {
            final Key key_temp = (Key) o;
            return key_temp.name.equals(name);
        } else {
            return false;
        }
    }

    @Override
    public final String toString() {
        return String.format("Key: \"%s\" == %d, isKeyboardKey: %b", name, key, isKeyboardKey);
    }

    @Override
    public final Element toXML() {
        final String key_name = GLFW.glfwGetKeyName(key, 0);
        final boolean isInt = (key_name == null || key_name.isEmpty());
        return new Element(getClass().getSimpleName()).setAttribute("name", name).setAttribute("key", (isInt) ? ("" + key) : key_name).setAttribute("isKeyboardKey", "" + isKeyboardKey).setAttribute("isInt", "" + isInt);
    }

}
