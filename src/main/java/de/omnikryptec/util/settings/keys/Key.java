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

package de.omnikryptec.util.settings.keys;

import java.util.Objects;

import de.codemakers.base.exceptions.NotYetImplementedRuntimeException;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.util.settings.KeySettings;

public class Key implements IKey {
    
    /**
     * Default {@link de.omnikryptec.util.settings.keys.Key} which can be returned
     * instead of null
     */
    public static final Key DEFAULT_NULL_KEY = new Key("DEFAULT_NULL_KEY", -1);
    
    protected final String name;
    protected int key;
    protected boolean isKeyboardKey;
    protected byte keyState = KeySettings.KEY_UNKNOWN;
    protected double lastUpdate = 0.0F;
    
    /**
     * Constructs a {@link de.omnikryptec.util.settings.keys.Key} (as a keyboard
     * key)
     *
     * @param name Name of the {@link de.omnikryptec.util.settings.keys.Key} (e.g.
     *             "Arrow Up")
     * @param key  KeyCode (e.g.
     *             {@link de.omnikryptec.util.settings.keys.KeysAndButtons#OKE_KEY_A})
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
     * @param key           KeyCode (e.g.
     *                      {@link de.omnikryptec.util.settings.keys.KeysAndButtons#OKE_KEY_A})
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
        return this.keyState == KeySettings.KEY_PRESSED || this.keyState == KeySettings.KEY_REPEATED; //TODO Panzer1119 Check this, is Repeated == Pressed?
    }
    
    /**
     * Returns the state of this {@link de.omnikryptec.util.settings.keys.Key}
     *
     * @return {@link de.omnikryptec.util.settings.KeySettings#KEY_UNKNOWN} or
     *         {@link de.omnikryptec.util.settings.KeySettings#KEY_NOTHING} or
     *         {@link de.omnikryptec.util.settings.KeySettings#KEY_PRESSED} or
     *         {@link de.omnikryptec.util.settings.KeySettings#KEY_REPEATED} or
     *         {@link de.omnikryptec.util.settings.KeySettings#KEY_RELEASED}
     */
    public byte getKeyState() {
        return this.keyState;
    }
    
    /**
     * Sets the state of this {@link de.omnikryptec.util.settings.keys.Key}
     *
     * @param keyState {@link de.omnikryptec.util.settings.KeySettings#KEY_UNKNOWN}
     *                 or
     *                 {@link de.omnikryptec.util.settings.KeySettings#KEY_NOTHING}
     *                 or
     *                 {@link de.omnikryptec.util.settings.KeySettings#KEY_PRESSED}
     *                 or
     *                 {@link de.omnikryptec.util.settings.KeySettings#KEY_REPEATED}
     *                 or
     *                 {@link de.omnikryptec.util.settings.KeySettings#KEY_RELEASED}
     *                 
     * @return A reference to this {@link de.omnikryptec.util.settings.keys.Key}
     */
    public Key setKeyState(final byte keyState) {
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
            throw new NotYetImplementedRuntimeException();
        }
        if (!isPressed()) {
            return false;
        }
        final double currentTime = LibAPIManager.instance().getGLFW().getTime();
        final double pressedTime = currentTime - this.lastUpdate;
        //this.lastChange = currentTime; //FIXME Panzer1119 lastChange/lastUpdate should just show the last time, when this key isPressed was updated. Why should this method reset this time?
        return (pressedTime >= minTime || minTime < 0) && (pressedTime <= maxTime || maxTime < 0);
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
     * @param key KeyCode (e.g.
     *            {@link de.omnikryptec.util.settings.keys.KeysAndButtons#OKE_KEY_A})
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
    public double getLastUpdate() { // TODO Panzer1119 Maybe use another System to determine long key presses
        return this.lastUpdate;
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
        return "Key{" + "name='" + this.name + '\'' + ", key=" + this.key + ", isKeyboardKey=" + this.isKeyboardKey
                + ", keyState=" + this.keyState + ", lastUpdate=" + this.lastUpdate + '}';
    }
    
}
