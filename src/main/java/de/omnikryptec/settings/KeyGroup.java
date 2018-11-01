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

import java.util.ArrayList;
import java.util.Objects;

public class KeyGroup implements IKey {
    
    private final String name;
    private final ArrayList<de.omnikryptec.old.settings.IKey> keys = new ArrayList<>();
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
     * @param allIKeysNeedsToBePressed True if all {@link de.omnikryptec.settings.IKey}s in this {@link de.omnikryptec.settings.KeyGroup} have to be pressed at the same time
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
        return false;
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
        return false;
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
