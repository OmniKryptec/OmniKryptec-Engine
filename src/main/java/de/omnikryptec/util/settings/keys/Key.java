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

package de.omnikryptec.util.settings.keys;

import java.util.Objects;

import de.codemakers.base.exceptions.NotYetImplementedRuntimeException;
import de.omnikryptec.libapi.exposed.LibAPIManager;

public class Key implements IKey {
    
    /**
     * Default {@link de.omnikryptec.util.settings.keys.Key} which can be returned
     * instead of null
     */
    public static final Key DEFAULT_NULL_KEY = new Key("DEFAULT_NULL_KEY", -1);
    
    private final String name;
    private int key;
    private boolean isKeyboardKey;
    private byte keyState;
    private double lastUpdate = 0.0F;
    
    /**
     * Constructs a {@link de.omnikryptec.util.settings.keys.Key} (as a keyboard
     * key)
     *
     * @param name Name of the {@link de.omnikryptec.util.settings.keys.Key} (e.g.
     *             "Arrow Up")
     * @param key  KeyCode (e.g. {@link org.lwjgl.glfw.GLFW#GLFW_KEY_A})
     */
    public Key(final String name, final int key) {
        this(name, key, true);
    }
    
    /**
     * Constructs a {@link de.omnikryptec.util.settings.keys.Key}
     *
     * @param name          Name of the
     *                      {@link de.omnikryptec.util.settings.keys.Key} (e.g.
     *                      "Arrow Up")
     * @param key           KeyCode (e.g. {@link org.lwjgl.glfw.GLFW#GLFW_KEY_A})
     * @param isKeyboardKey <tt>true</tt> if the
     *                      {@link de.omnikryptec.util.settings.keys.Key} is a
     *                      keyboard key
     */
    public Key(final String name, final int key, final boolean isKeyboardKey) {
        this.name = name;
        this.key = key;
        this.isKeyboardKey = isKeyboardKey;
    }
    
    /**
     * Returns the name of the {@link de.omnikryptec.util.settings.keys.Key}
     *
     * @return Name of the {@link de.omnikryptec.util.settings.keys.Key}
     */
    @Override
    public String getName() {
        return this.name;
    }
    
    /**
     * Returns if this {@link de.omnikryptec.util.settings.keys.Key} is being
     * pressed
     *
     * @return <tt>true</tt> if this {@link de.omnikryptec.util.settings.keys.Key}
     *         is pressed
     */
    @Override
    public boolean isPressed() {
        return keyState == KeySettings.KEY_PRESSED || keyState == KeySettings.KEY_REPEATED; //TODO Check this, is Repeated == Pressed?
    }
    
    /*
    public Key setPressed(boolean isPressed) {
        this.isPressed = isPressed;
        return this;
    }
    */
    
    public byte getKeyState() {
        return keyState;
    }
    
    public Key setKeyState(byte keyState) {
        this.keyState = keyState;
        return this;
    }
    
    /**
     * Returns if this {@link de.omnikryptec.util.settings.keys.Key} is being
     * pressed for a specified time
     *
     * @param minTime Minimum pressing time
     * @param maxTime Maximum pressing time
     *
     * @return <tt>true</tt> if this {@link de.omnikryptec.util.settings.keys.Key}
     *         is pressed for the specified time
     */
    @Override
    public boolean isLongPressed(final double minTime, final double maxTime) {
        if (true) {
            throw new NotYetImplementedRuntimeException(); //FIXME Remove this
        }
        if (!isPressed()) {
            return false;
        }
        final double currentTime = LibAPIManager.instance().getTime();
        final double pressedTime = currentTime - this.lastUpdate;
        if ((pressedTime >= minTime || minTime < 0) && (pressedTime <= maxTime || maxTime < 0)) {
            //this.lastChange = currentTime; //FIXME lastChange/lastUpdate should just show the last time, when this key isPressed was updated. Why should this method reset this time?
            return true;
        }
        return false;
    }
    
    /**
     * Returns the KeyCode
     *
     * @return KeyCode
     */
    public int getKey() {
        return this.key;
    }
    
    /**
     * Sets the KeyCode
     *
     * @param key KeyCode (e.g. {@link org.lwjgl.glfw.GLFW#GLFW_KEY_A})
     *
     * @return A reference to this {@link de.omnikryptec.util.settings.keys.Key}
     */
    public Key setKey(final int key) {
        this.key = key;
        return this;
    }
    
    /**
     * Returns if this {@link de.omnikryptec.util.settings.keys.Key} is a keyboard
     * key
     *
     * @return <tt>true</tt> if this {@link de.omnikryptec.util.settings.keys.Key}
     *         is a keyboard key
     */
    public boolean isKeyboardKey() {
        return this.isKeyboardKey;
    }
    
    /**
     * @param keyboardKey <tt>true</tt> if this
     *                    {@link de.omnikryptec.util.settings.keys.Key} is a
     *                    keyboard key
     *
     * @return A reference to this {@link de.omnikryptec.util.settings.keys.Key}
     */
    public Key setKeyboardKey(final boolean keyboardKey) {
        this.isKeyboardKey = keyboardKey;
        return this;
    }
    
    /**
     * Returns the last update of this {@link de.omnikryptec.util.settings.keys.Key}
     *
     * @return Last update of this {@link de.omnikryptec.util.settings.keys.Key}
     */
    public double getLastUpdate() { // FIXME Maybe use another System to determine long key presses
        return lastUpdate;
    }
    
    /**
     * Sets the last update of this {@link de.omnikryptec.util.settings.keys.Key}
     *
     * @param lastUpdate Last update of this
     *                   {@link de.omnikryptec.util.settings.keys.Key}
     *
     * @return A reference to this {@link de.omnikryptec.util.settings.keys.Key}
     */
    public Key setLastUpdate(final double lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !getClass().isAssignableFrom(o.getClass())) {
            return false;
        }
        final Key key1 = (Key) o;
        return this.key == key1.key && this.isKeyboardKey == key1.isKeyboardKey && Objects.equals(this.name, key1.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.key, this.isKeyboardKey);
    }
    
    @Override
    public String toString() {
        return "Key{" + "name='" + this.name + '\'' + ", key=" + this.key + ", isKeyboardKey=" + this.isKeyboardKey + ", isPressed=" + this.isPressed + ", lastUpdate=" + this.lastUpdate + '}';
    }
    
}
