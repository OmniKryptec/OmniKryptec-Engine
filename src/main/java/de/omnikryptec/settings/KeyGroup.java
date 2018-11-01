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

package de.omnikryptec.settings;

import de.codemakers.base.exceptions.NotYetImplementedRuntimeException;

import java.util.*;

public class KeyGroup implements IKey {
    
    /**
     * Default {@link de.omnikryptec.settings.KeyGroup} which can be returned instead of null
     */
    public static final KeyGroup DEFAULT_NULL_KEY_GROUP = new KeyGroup("DEFAULT_NULL_KEY_GROUP");
    
    private final String name;
    private final List<IKey> keys = new ArrayList<>();
    private boolean allIKeysNeedsToBePressed;
    
    /**
     * Constructs a {@link de.omnikryptec.settings.KeyGroup} (where all {@link de.omnikryptec.settings.IKey}s needs to be pressed at the same time)
     * <p>
     * A KeyGroup contains IKeys, which means a KeyGroup can contain multiple {@link de.omnikryptec.settings.KeyGroup}s or Keys
     *
     * @param name Name of the {@link de.omnikryptec.settings.KeyGroup}
     */
    public KeyGroup(String name) {
        this(name, true);
    }
    
    /**
     * Constructs a {@link de.omnikryptec.settings.KeyGroup}
     * <p>
     * A KeyGroup contains IKeys, which means a KeyGroup can contain multiple {@link de.omnikryptec.settings.KeyGroup}s or Keys
     *
     * @param name Name of the {@link de.omnikryptec.settings.KeyGroup}
     * @param allIKeysNeedsToBePressed <tt>true</tt> if all {@link de.omnikryptec.settings.IKey}s in this {@link de.omnikryptec.settings.KeyGroup} have to be pressed at the same time
     */
    public KeyGroup(String name, boolean allIKeysNeedsToBePressed) {
        this.name = name;
        this.allIKeysNeedsToBePressed = allIKeysNeedsToBePressed;
    }
    
    /**
     * Returns the name of the {@link de.omnikryptec.settings.KeyGroup}
     *
     * @return Name of the {@link de.omnikryptec.settings.KeyGroup}
     */
    @Override
    public String getName() {
        return null;
    }
    
    /**
     * Returns if this {@link de.omnikryptec.settings.KeyGroup} is being pressed
     *
     * @return <tt>true</tt> if this {@link de.omnikryptec.settings.KeyGroup} is pressed
     */
    @Override
    public boolean isPressed() {
        if (keys.isEmpty()) {
            return false;
        }
        boolean isPressed = allIKeysNeedsToBePressed;
        for (IKey key : keys) {
            final boolean isPressed_ = key.isPressed();
            if (!isPressed_ && allIKeysNeedsToBePressed) {
                isPressed = false;
                break;
            } else if (isPressed_ && !allIKeysNeedsToBePressed) {
                isPressed = true;
                break;
            }
        }
        return isPressed;
    }
    
    /**
     * Returns if this {@link de.omnikryptec.settings.KeyGroup} is being pressed for a specified time
     *
     * @param minTime Float Minimum pressing time
     * @param maxTime Float Maximum pressing time
     *
     * @return <tt>true</tt> if this {@link de.omnikryptec.settings.KeyGroup} is pressed for the specified time
     */
    @Override
    public boolean isLongPressed(double minTime, double maxTime) {
        if (keys.isEmpty()) {
            return false;
        }
        //TODO Implement
        throw new NotYetImplementedRuntimeException();
    }
    
    /**
     * Returns all {@link de.omnikryptec.settings.IKey}s in this {@link de.omnikryptec.settings.KeyGroup}
     *
     * @return {@link de.omnikryptec.settings.IKey}s in this {@link de.omnikryptec.settings.KeyGroup}
     */
    public List<IKey> getKeys() {
        return keys;
    }
    
    /**
     * Adds some {@link de.omnikryptec.settings.IKey}s to this {@link de.omnikryptec.settings.KeyGroup}
     *
     * @param keys {@link de.omnikryptec.settings.IKey}s to be added
     *
     * @return A reference to this {@link de.omnikryptec.settings.KeyGroup}
     */
    public KeyGroup addKeys(IKey... keys) {
        this.keys.addAll(Arrays.asList(keys));
        return this;
    }
    
    /**
     * Adds some {@link de.omnikryptec.settings.IKey}s to this {@link de.omnikryptec.settings.KeyGroup}
     *
     * @param keys {@link de.omnikryptec.settings.IKey}s to be added
     *
     * @return A reference to this {@link de.omnikryptec.settings.KeyGroup}
     */
    public KeyGroup addKeys(Collection<IKey> keys) {
        this.keys.addAll(keys);
        return this;
    }
    
    /**
     * Returns if all {@link de.omnikryptec.settings.IKey}s in this {@link de.omnikryptec.settings.KeyGroup} needs to be pressed at the same time
     *
     * @return <tt>true</tt> if all {@link de.omnikryptec.settings.IKey}s in this {@link de.omnikryptec.settings.KeyGroup} needs to be pressed at the same time
     */
    public boolean isAllIKeysNeedsToBePressed() {
        return allIKeysNeedsToBePressed;
    }
    
    /**
     * Sets if all {@link de.omnikryptec.settings.IKey}s in this {@link de.omnikryptec.settings.KeyGroup} needs to be pressed at the same time
     *
     * @param allIKeysNeedsToBePressed <tt>true</tt> if all {@link de.omnikryptec.settings.IKey}s in this {@link de.omnikryptec.settings.KeyGroup} needs to be pressed at the same time
     *
     * @return A reference to this {@link de.omnikryptec.settings.KeyGroup}
     */
    public KeyGroup setAllIKeysNeedsToBePressed(boolean allIKeysNeedsToBePressed) {
        this.allIKeysNeedsToBePressed = allIKeysNeedsToBePressed;
        return this;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !getClass().isAssignableFrom(o.getClass())) {
            return false;
        }
        final KeyGroup keyGroup = (KeyGroup) o;
        return allIKeysNeedsToBePressed == keyGroup.allIKeysNeedsToBePressed && Objects.equals(name, keyGroup.name) && Objects.equals(keys, keyGroup.keys);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, keys, allIKeysNeedsToBePressed);
    }
    
    @Override
    public String toString() {
        return "KeyGroup{" + "name='" + name + '\'' + ", keys=" + keys + ", allIKeysNeedsToBePressed=" + allIKeysNeedsToBePressed + '}';
    }
    
}
