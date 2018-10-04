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

import java.util.ArrayList;
import java.util.Arrays;

import org.jdom2.Element;

/**
 * Key group
 *
 * @author Panzer1119
 */
public class KeyGroup implements IKey {

    /**
     * Default KeyGroup which gets returned instead of null
     */
    public static final KeyGroup DEFAULT_NULL_KEYGROUP = new KeyGroup("DEFAULT_NULL_KEYGROUP");

    private final String name;
    private final ArrayList<IKey> keys = new ArrayList<>();
    private boolean allKeysNeedToBeActivated = true;

    /**
     * Constructs a KeyGroup
     *
     * @param name String Name
     * @param keys Key Array First Keys
     */
    public KeyGroup(String name, Key... keys) {
        this.name = name;
        addKeys(keys);
    }

    @Override
    public final String getName() {
        return name;
    }

    /**
     * Returns if all Keys in this KeyGroup are needed to be activated
     *
     * @return <tt>true</tt> if all Keys in this KeyGroup are needed to be
     * activated
     */
    public final boolean isAllKeysNeedToBeActivated() {
        return allKeysNeedToBeActivated;
    }

    /**
     * Sets if all Keys in this KeyGroup are needed to be activated
     *
     * @param allKeysNeedToBeActivated Boolean If all Keys in this KeyGroup are
     * needed to be activated
     * @return KeyGroup A reference to this KeyGroup
     */
    public final KeyGroup setAllKeysNeedToBeActivated(boolean allKeysNeedToBeActivated) {
        this.allKeysNeedToBeActivated = allKeysNeedToBeActivated;
        return this;
    }

    /**
     * Returns the IKeys
     *
     * @return ArrayList IKey IKeys
     */
    public final ArrayList<IKey> getKeys() {
        return keys;
    }

    /**
     * Adds an IKey
     *
     * @param key IKey IKey to add
     * @return KeyGroup A reference to this KeyGroup
     */
    public final KeyGroup addKey(Key key) {
        this.keys.add(key);
        return this;
    }

    /**
     * Adds multiple IKeys
     *
     * @param keys IKey Array IKeys to add
     * @return KeyGroup A reference to this KeyGroup
     */
    public final KeyGroup addKeys(IKey[] keys) {
        if (keys == null || keys.length == 0) {
            return this;
        }
        for (IKey key : keys) {
            this.keys.add(key);
        }
        return this;
    }

    /**
     * Adds multiple IKeys
     *
     * @param keys ArrayList IKey IKeys to add
     * @return KeyGroup A reference to this KeyGroup
     */
    public final KeyGroup addKeys(ArrayList<IKey> keys) {
        if (keys == null || keys.isEmpty()) {
            return this;
        }
        for (IKey key : keys) {
            this.keys.add(key);
        }
        return this;
    }

    @Override
    public final boolean isPressed() {
        if (keys.isEmpty()) {
            return false;
        }
        boolean isPressed = allKeysNeedToBeActivated;
        for (IKey key : keys) {
            final boolean isPressed_ = key.isPressed();
            if (!isPressed_ && allKeysNeedToBeActivated) {
                isPressed = false;
                break;
            } else if (isPressed_ && !allKeysNeedToBeActivated) {
                isPressed = true;
                break;
            }
        }
        return isPressed;
    }

    @Override
    public final boolean isLongPressed(double minTime, double maxTime) {
        if (keys.isEmpty()) {
            return false;
        }
        boolean isLongPressed = allKeysNeedToBeActivated;
        boolean firstRight = false;
        for (IKey key : keys) {
            final boolean isPressed = key.isPressed();
            final boolean isLongPressed_ = key.isLongPressed(minTime, maxTime);
            if (!isLongPressed_ && allKeysNeedToBeActivated && !(firstRight && isPressed)) {
                isLongPressed = false;
                break;
            } else if (isLongPressed_ && allKeysNeedToBeActivated) {
                firstRight = true;
            } else if (isLongPressed_ && !allKeysNeedToBeActivated) {
                isLongPressed = true;
                break;
            }
        }
        return isLongPressed;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof KeyGroup) {
            final KeyGroup keyGroup_temp = (KeyGroup) o;
            return keyGroup_temp.name.equals(name);
        } else {
            return false;
        }
    }

    @Override
    public final String toString() {
        return String.format("KeyGroup: \"%s\" (%d): %s", name, keys.size(), Arrays.toString(keys.toArray()));
    }

    @Override
    public final Element toXML() {
        final Element element = new Element(getClass().getSimpleName());
        element.setAttribute("name", name);
        keys.forEach((key) -> element.addContent(key.toXML()));
        element.setAttribute("allKeysNeedToBeActivated", "" + allKeysNeedToBeActivated);
        return element;
    }

}
