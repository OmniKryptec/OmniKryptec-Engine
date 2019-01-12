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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import de.omnikryptec.util.Util;

/**
 * This class is just for the {@link de.omnikryptec.util.settings.KeySettings}
 * and {@link de.omnikryptec.util.settings.keys.KeyGroup} class
 */
public class KeyContainer {
    
    /**
     * Default {@link de.omnikryptec.util.settings.keys.IKey} which can be returned
     * instead of null
     */
    public static final IKey DEFAULT_NULL_IKEY = new IKey() {
        @Override
        public String getName() {
            return null;
        }
        
        @Override
        public boolean isPressed() {
            return false;
        }
        
        @Override
        public boolean isLongPressed(final double minTime, final double maxTime) {
            return false;
        }
    };
    
    final BiMap<String, IKey> keys = HashBiMap.create();
    
    /**
     * Returns the {@link com.google.common.collect.BiMap<String,
     * de.omnikryptec.util.settings.keys.IKey>} of this
     * {@link de.omnikryptec.util.settings.keys.KeyContainer}
     *
     * @return {@link com.google.common.collect.BiMap<String,
     *         de.omnikryptec.util.settings.keys.IKey>}
     */
    BiMap<String, IKey> getIKeysBiMap() {
        return this.keys;
    }
    
    /**
     * Returns all {@link de.omnikryptec.util.settings.keys.IKey}s of this
     * {@link de.omnikryptec.util.settings.keys.KeyContainer}
     *
     * @return {@link de.omnikryptec.util.settings.keys.IKey}s
     */
    public Set<IKey> getIKeys() {
        return this.keys.values();
    }
    
    /**
     * Sets the {@link de.omnikryptec.util.settings.keys.IKey}s of this
     * {@link de.omnikryptec.util.settings.keys.KeyContainer}
     *
     * @param keys {@link de.omnikryptec.util.settings.keys.IKey}s
     *
     * @return A reference to this
     *         {@link de.omnikryptec.util.settings.keys.KeyContainer}
     */
    public KeyContainer setIKeys(final List<IKey> keys) {
        Util.ensureNonNull(keys);
        this.keys.clear();
        keys.forEach(this::addIKey);
        return this;
    }
    
    /**
     * Adds a {@link de.omnikryptec.util.settings.keys.IKey} to this
     * {@link de.omnikryptec.util.settings.keys.KeyContainer}
     *
     * @param key {@link de.omnikryptec.util.settings.keys.IKey} to be added
     *
     * @return A reference to this
     *         {@link de.omnikryptec.util.settings.keys.KeyContainer}
     */
    public KeyContainer addIKey(final IKey key) {
        Util.ensureNonNull(key);
        this.keys.put(key.getName(), key);
        return this;
    }
    
    /**
     * Adds some {@link de.omnikryptec.util.settings.keys.IKey}s to this
     * {@link de.omnikryptec.util.settings.keys.KeyContainer}
     *
     * @param keys {@link de.omnikryptec.util.settings.keys.IKey}s to be added
     *
     * @return A reference to this
     *         {@link de.omnikryptec.util.settings.keys.KeyContainer}
     */
    public KeyContainer addIKeys(final IKey... keys) {
        return addIKeys(Arrays.asList(keys));
    }
    
    /**
     * Adds some {@link de.omnikryptec.util.settings.keys.IKey}s to this
     * {@link de.omnikryptec.util.settings.keys.KeyContainer}
     *
     * @param keys {@link de.omnikryptec.util.settings.keys.IKey}s to be added
     *
     * @return A reference to this
     *         {@link de.omnikryptec.util.settings.keys.KeyContainer}
     */
    public KeyContainer addIKeys(final Collection<IKey> keys) {
        Util.ensureNonNull(keys);
        keys.forEach(this::addIKey);
        return this;
    }
    
    /**
     * Removes a {@link de.omnikryptec.util.settings.keys.IKey} specified by its
     * name
     *
     * @param name Name of the {@link de.omnikryptec.util.settings.keys.IKey} to get
     *             removed
     *
     * @return A reference to this
     *         {@link de.omnikryptec.util.settings.keys.KeyContainer}
     */
    public KeyContainer removeIKey(final String name) {
        this.keys.remove(name);
        return this;
    }
    
    /**
     * Removes a {@link de.omnikryptec.util.settings.keys.IKey}
     *
     * @param key {@link de.omnikryptec.util.settings.keys.IKey} to get removed
     *
     * @return A reference to this
     *         {@link de.omnikryptec.util.settings.keys.KeyContainer}
     */
    public KeyContainer removeIKey(final IKey key) {
        Util.ensureNonNull(key);
        this.keys.remove(key.getName());
        return this;
    }
    
    // TODO Javadoc!
    
    public KeyContainer addKey(final String name, final int keyCode) {
        return addIKey(new Key(name, keyCode));
    }
    
    public KeyContainer addKey(final String name, final int keyCode, final boolean isKeyboardKey) {
        return addIKey(new Key(name, keyCode, isKeyboardKey));
    }
    
    // TODO Javadoc!
    
    public <T extends IKey> T getIKey(final String name) {
        return getIKey(name, null);
    }
    
    public <T extends IKey> T getIKey(final String name, final T defaultValue) {
        final IKey key = this.keys.get(name);
        return key == null ? defaultValue : (T) key;
    }
    
    public Key getKey(final String name) {
        return (Key) this.keys.get(name);
    }
    
    public List<Key> getKeys() {
        return this.keys.values().stream().filter((key) -> key instanceof Key).map((key) -> (Key) key)
                .collect(Collectors.toList());
    }
    
    public List<Key> getKeys(final boolean isKeyboardKey) {
        return this.keys.values().stream().filter((key) -> key instanceof Key).map((key) -> (Key) key)
                .filter((key) -> key.isKeyboardKey() == isKeyboardKey).collect(Collectors.toList());
    }
    
    public KeyGroup getKeyGroup(final String name) {
        return (KeyGroup) this.keys.get(name);
    }
    
    public List<KeyGroup> getKeyGroups() {
        return this.keys.values().stream().filter((key) -> key instanceof KeyGroup).map((key) -> (KeyGroup) key)
                .collect(Collectors.toList());
    }
    
    /**
     * Returns <tt>true</tt> if this
     * {@link de.omnikryptec.util.settings.keys.KeyContainer} is empty
     *
     * @return <tt>true</tt> if this
     *         {@link de.omnikryptec.util.settings.keys.KeyContainer} is empty
     */
    public boolean isEmpty() {
        return this.keys.isEmpty();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof KeyContainer)) {
            return false;
        }
        final KeyContainer that = (KeyContainer) o;
        return Objects.equals(this.keys, that.keys);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.keys);
    }
    
    @Override
    public String toString() {
        return "KeyContainer{" + "keys=" + this.keys + '}';
    }
    
}
