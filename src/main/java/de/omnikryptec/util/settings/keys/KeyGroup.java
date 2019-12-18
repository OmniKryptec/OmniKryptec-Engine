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

import java.util.Collection;
import java.util.Objects;

public class KeyGroup extends KeyContainer implements IKey {
    
    /**
     * Default {@link de.omnikryptec.util.settings.keys.KeyGroup} which can be
     * returned instead of null
     */
    public static final KeyGroup DEFAULT_NULL_KEY_GROUP = new KeyGroup("DEFAULT_NULL_KEY_GROUP");
    
    protected final String name;
    protected boolean allIKeysNeedsToBePressed;
    
    /**
     * Constructs a {@link de.omnikryptec.util.settings.keys.KeyGroup} (where all
     * {@link de.omnikryptec.util.settings.keys.IKey}s needs to be pressed at the
     * same time)
     * <p>
     * A KeyGroup contains {@link de.omnikryptec.util.settings.keys.IKey}s, which
     * means a {@link de.omnikryptec.util.settings.keys.KeyGroup} can contain
     * multiple {@link de.omnikryptec.util.settings.keys.KeyGroup}s or
     * {@link de.omnikryptec.util.settings.keys.Key}s
     *
     * @param name Name of the {@link de.omnikryptec.util.settings.keys.KeyGroup}
     */
    public KeyGroup(final String name) {
        this(name, true);
    }
    
    /**
     * Constructs a {@link de.omnikryptec.util.settings.keys.KeyGroup}
     * <p>
     * A KeyGroup contains {@link de.omnikryptec.util.settings.keys.IKey}s, which
     * means a {@link de.omnikryptec.util.settings.keys.KeyGroup} can contain
     * multiple {@link de.omnikryptec.util.settings.keys.KeyGroup}s or
     * {@link de.omnikryptec.util.settings.keys.Key}s
     *
     * @param name                     Name of the
     *                                 {@link de.omnikryptec.util.settings.keys.KeyGroup}
     * @param allIKeysNeedsToBePressed <tt>true</tt> if all
     *                                 {@link de.omnikryptec.util.settings.keys.IKey}s
     *                                 in this
     *                                 {@link de.omnikryptec.util.settings.keys.KeyGroup}
     *                                 have to be pressed at the same time
     */
    public KeyGroup(final String name, final boolean allIKeysNeedsToBePressed) {
        this(name, allIKeysNeedsToBePressed, null);
    }
    
    /**
     * Constructs a {@link de.omnikryptec.util.settings.keys.KeyGroup}
     * <p>
     * A KeyGroup contains {@link de.omnikryptec.util.settings.keys.IKey}s, which
     * means a {@link de.omnikryptec.util.settings.keys.KeyGroup} can contain
     * multiple {@link de.omnikryptec.util.settings.keys.KeyGroup}s or
     * {@link de.omnikryptec.util.settings.keys.Key}s
     *
     * @param name                     Name of the
     *                                 {@link de.omnikryptec.util.settings.keys.KeyGroup}
     * @param allIKeysNeedsToBePressed <tt>true</tt> if all
     *                                 {@link de.omnikryptec.util.settings.keys.IKey}s
     *                                 in this
     *                                 {@link de.omnikryptec.util.settings.keys.KeyGroup}
     *                                 have to be pressed at the same time
     * @param ikeys                    {@link de.omnikryptec.util.settings.keys.IKey}s
     *                                 to be added
     */
    public KeyGroup(final String name, final boolean allIKeysNeedsToBePressed, final Collection<IKey> ikeys) {
        this.name = name;
        this.allIKeysNeedsToBePressed = allIKeysNeedsToBePressed;
        if (ikeys != null) {
            addIKeys(ikeys);
        }
    }
    
    /**
     * Returns the name of the {@link de.omnikryptec.util.settings.keys.KeyGroup}
     *
     * @return Name of the {@link de.omnikryptec.util.settings.keys.KeyGroup}
     */
    @Override
    public String getName() {
        return this.name;
    }
    
    /**
     * Returns if this {@link de.omnikryptec.util.settings.keys.KeyGroup} is being
     * pressed
     *
     * @return <tt>true</tt> if this
     *         {@link de.omnikryptec.util.settings.keys.KeyGroup} is pressed
     */
    @Override
    public boolean isPressed() {
        if (isEmpty()) {
            return false;
        }
        for (final IKey ikey : getIKeys()) {
            final boolean isPressed = ikey.isPressed();
            if (!isPressed && this.allIKeysNeedsToBePressed) {
                return false;
            } else if (isPressed && !this.allIKeysNeedsToBePressed) {
                return true;
            }
        }
        return this.allIKeysNeedsToBePressed;
    }
    
    /**
     * Returns if this {@link de.omnikryptec.util.settings.keys.KeyGroup} is being
     * pressed for a specified time
     *
     * @param minTime Minimum pressing time
     * @param maxTime Maximum pressing time
     *
     * @return <tt>true</tt> if this
     *         {@link de.omnikryptec.util.settings.keys.KeyGroup} is pressed for the
     *         specified time
     */
    @Override
    public boolean isLongPressed(final double minTime, final double maxTime) {
        if (isEmpty()) {
            return false;
        }
        for (final IKey ikey : getIKeys()) {
            final boolean isLongPressed = ikey.isLongPressed(minTime, maxTime);
            if (!isLongPressed && this.allIKeysNeedsToBePressed) {
                return false;
            } else if (isLongPressed && !this.allIKeysNeedsToBePressed) {
                return true;
            }
        }
        return this.allIKeysNeedsToBePressed;
    }
    
    /**
     * Returns if all {@link de.omnikryptec.util.settings.keys.IKey}s in this
     * {@link de.omnikryptec.util.settings.keys.KeyGroup} needs to be pressed at the
     * same time
     *
     * @return <tt>true</tt> if all {@link de.omnikryptec.util.settings.keys.IKey}s
     *         in this {@link de.omnikryptec.util.settings.keys.KeyGroup} needs to
     *         be pressed at the same time
     */
    public boolean isAllIKeysNeedsToBePressed() {
        return this.allIKeysNeedsToBePressed;
    }
    
    /**
     * Sets if all {@link de.omnikryptec.util.settings.keys.IKey}s in this
     * {@link de.omnikryptec.util.settings.keys.KeyGroup} needs to be pressed at the
     * same time
     *
     * @param allIKeysNeedsToBePressed <tt>true</tt> if all
     *                                 {@link de.omnikryptec.util.settings.keys.IKey}s
     *                                 in this
     *                                 {@link de.omnikryptec.util.settings.keys.KeyGroup}
     *                                 needs to be pressed at the same time
     *
     * @return A reference to this
     *         {@link de.omnikryptec.util.settings.keys.KeyGroup}
     */
    public KeyGroup setAllIKeysNeedsToBePressed(final boolean allIKeysNeedsToBePressed) {
        this.allIKeysNeedsToBePressed = allIKeysNeedsToBePressed;
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
        if (!super.equals(o)) {
            return false;
        }
        final KeyGroup keyGroup = (KeyGroup) o;
        return this.allIKeysNeedsToBePressed == keyGroup.allIKeysNeedsToBePressed
                && Objects.equals(this.name, keyGroup.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.name, this.allIKeysNeedsToBePressed);
    }
    
    @Override
    public String toString() {
        return "KeyGroup{" + "name='" + this.name + '\'' + ", allIKeysNeedsToBePressed=" + this.allIKeysNeedsToBePressed
                + ", ikeys=" + this.ikeys + '}';
    }
    
}
