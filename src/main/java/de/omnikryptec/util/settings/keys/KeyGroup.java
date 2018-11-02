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

package de.omnikryptec.util.settings.keys;

import java.util.Objects;

public class KeyGroup extends KeyContainer implements IKey {
    
    /**
     * Default {@link de.omnikryptec.util.settings.keys.KeyGroup} which can be returned instead of null
     */
    public static final KeyGroup DEFAULT_NULL_KEY_GROUP = new KeyGroup("DEFAULT_NULL_KEY_GROUP");
    
    private final String name;
    private boolean allIKeysNeedsToBePressed;
    
    /**
     * Constructs a {@link de.omnikryptec.util.settings.keys.KeyGroup} (where all {@link de.omnikryptec.util.settings.keys.IKey}s needs to be pressed at the same time)
     * <p>
     * A KeyGroup contains IKeys, which means a KeyGroup can contain multiple {@link de.omnikryptec.util.settings.keys.KeyGroup}s or Keys
     *
     * @param name Name of the {@link de.omnikryptec.util.settings.keys.KeyGroup}
     */
    public KeyGroup(String name) {
        this(name, true);
    }
    
    /**
     * Constructs a {@link de.omnikryptec.util.settings.keys.KeyGroup}
     * <p>
     * A KeyGroup contains IKeys, which means a KeyGroup can contain multiple {@link de.omnikryptec.util.settings.keys.KeyGroup}s or Keys
     *
     * @param name Name of the {@link de.omnikryptec.util.settings.keys.KeyGroup}
     * @param allIKeysNeedsToBePressed <tt>true</tt> if all {@link de.omnikryptec.util.settings.keys.IKey}s in this {@link de.omnikryptec.util.settings.keys.KeyGroup} have to be pressed at the same time
     */
    public KeyGroup(String name, boolean allIKeysNeedsToBePressed) {
        this.name = name;
        this.allIKeysNeedsToBePressed = allIKeysNeedsToBePressed;
    }
    
    /**
     * Returns the name of the {@link de.omnikryptec.util.settings.keys.KeyGroup}
     *
     * @return Name of the {@link de.omnikryptec.util.settings.keys.KeyGroup}
     */
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * Returns if this {@link de.omnikryptec.util.settings.keys.KeyGroup} is being pressed
     *
     * @return <tt>true</tt> if this {@link de.omnikryptec.util.settings.keys.KeyGroup} is pressed
     */
    @Override
    public boolean isPressed() {
        if (isEmpty()) {
            return false;
        }
        boolean isPressed = allIKeysNeedsToBePressed;
        for (IKey key : getKeys()) {
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
     * Returns if this {@link de.omnikryptec.util.settings.keys.KeyGroup} is being pressed for a specified time
     *
     * @param minTime Minimum pressing time
     * @param maxTime Maximum pressing time
     *
     * @return <tt>true</tt> if this {@link de.omnikryptec.util.settings.keys.KeyGroup} is pressed for the specified time
     */
    @Override
    public boolean isLongPressed(double minTime, double maxTime) {
        if (isEmpty()) {
            return false;
        }
        boolean isLongPressed = allIKeysNeedsToBePressed;
        //boolean firstPressed = false;
        for (IKey key : getKeys()) {
            //final boolean isPressed_ = key.isPressed();
            final boolean isLongPressed_ = key.isLongPressed(minTime, maxTime);
            /*
            if (!isLongPressed_ && allIKeysNeedsToBePressed && !(firstPressed && isPressed_)) {
                isLongPressed = false;
                break;
            } else if (isLongPressed_ && allIKeysNeedsToBePressed) {
                firstPressed = true;
            } else if (isLongPressed_) {
                isLongPressed = true;
                break;
            }
            //FIXME Why do we need 'firstPressed'??? Until we know why, keep the snippet below...
            */
            if (isLongPressed_ && !allIKeysNeedsToBePressed) {
                isLongPressed = true;
                break;
            } else if (!isLongPressed_ && !allIKeysNeedsToBePressed) {
                isLongPressed = false;
                break;
            }
        }
        return isLongPressed;
    }
    
    /**
     * Returns if all {@link de.omnikryptec.util.settings.keys.IKey}s in this {@link de.omnikryptec.util.settings.keys.KeyGroup} needs to be pressed at the same time
     *
     * @return <tt>true</tt> if all {@link de.omnikryptec.util.settings.keys.IKey}s in this {@link de.omnikryptec.util.settings.keys.KeyGroup} needs to be pressed at the same time
     */
    public boolean isAllIKeysNeedsToBePressed() {
        return allIKeysNeedsToBePressed;
    }
    
    /**
     * Sets if all {@link de.omnikryptec.util.settings.keys.IKey}s in this {@link de.omnikryptec.util.settings.keys.KeyGroup} needs to be pressed at the same time
     *
     * @param allIKeysNeedsToBePressed <tt>true</tt> if all {@link de.omnikryptec.util.settings.keys.IKey}s in this {@link de.omnikryptec.util.settings.keys.KeyGroup} needs to be pressed at the same time
     *
     * @return A reference to this {@link de.omnikryptec.util.settings.keys.KeyGroup}
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
        if (!super.equals(o)) {
            return false;
        }
        final KeyGroup keyGroup = (KeyGroup) o;
        return allIKeysNeedsToBePressed == keyGroup.allIKeysNeedsToBePressed && Objects.equals(name, keyGroup.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, allIKeysNeedsToBePressed);
    }
    
    @Override
    public String toString() {
        return "KeyGroup{" + "name='" + name + '\'' + ", allIKeysNeedsToBePressed=" + allIKeysNeedsToBePressed + ", keys=" + keys + '}';
    }
    
}
