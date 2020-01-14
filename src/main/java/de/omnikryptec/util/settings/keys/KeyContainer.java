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
 * This class is just for a super class for
 * {@link de.omnikryptec.util.settings.KeySettings} and
 * {@link de.omnikryptec.util.settings.keys.KeyGroup}
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
    
    protected final BiMap<String, IKey> ikeys = HashBiMap.create();
    
    public KeyContainer() {
    }
    
    public KeyContainer(final Collection<IKey> ikeys) {
        if (ikeys != null) {
            addIKeys(ikeys);
        }
    }
    
    /**
     * Returns the {@link com.google.common.collect.BiMap<String,
     * de.omnikryptec.util.settings.keys.IKey>} of this
     * {@link de.omnikryptec.util.settings.keys.KeyContainer}
     *
     * @return {@link com.google.common.collect.BiMap<String,
     *         de.omnikryptec.util.settings.keys.IKey>}
     */
    protected BiMap<String, IKey> getIKeysBiMap() {
        return this.ikeys;
    }
    
    /**
     * Returns all {@link de.omnikryptec.util.settings.keys.IKey}s of this
     * {@link de.omnikryptec.util.settings.keys.KeyContainer}
     *
     * @return {@link de.omnikryptec.util.settings.keys.IKey}s
     */
    public Set<IKey> getIKeys() {
        return this.ikeys.values();
    }
    
    /**
     * Sets the {@link de.omnikryptec.util.settings.keys.IKey}s of this
     * {@link de.omnikryptec.util.settings.keys.KeyContainer}
     *
     * @param ikeys {@link de.omnikryptec.util.settings.keys.IKey}s
     *
     * @return A reference to this
     *         {@link de.omnikryptec.util.settings.keys.KeyContainer}
     */
    public KeyContainer setIKeys(final Collection<IKey> ikeys) {
        Util.ensureNonNull(ikeys);
        this.ikeys.clear();
        addIKeys(ikeys);
        return this;
    }
    
    /**
     * Adds a {@link de.omnikryptec.util.settings.keys.IKey} to this
     * {@link de.omnikryptec.util.settings.keys.KeyContainer}
     *
     * @param ikey {@link de.omnikryptec.util.settings.keys.IKey} to be added
     *
     * @return A reference to this
     *         {@link de.omnikryptec.util.settings.keys.KeyContainer}
     */
    public KeyContainer addIKey(final IKey ikey) {
        Util.ensureNonNull(ikey);
        this.ikeys.put(ikey.getName(), ikey);
        return this;
    }
    
    /**
     * Adds some {@link de.omnikryptec.util.settings.keys.IKey}s to this
     * {@link de.omnikryptec.util.settings.keys.KeyContainer}
     *
     * @param ikeys {@link de.omnikryptec.util.settings.keys.IKey}s to be added
     *
     * @return A reference to this
     *         {@link de.omnikryptec.util.settings.keys.KeyContainer}
     */
    public KeyContainer addIKeys(final IKey... ikeys) {
        return addIKeys(Arrays.asList(ikeys));
    }
    
    /**
     * Adds some {@link de.omnikryptec.util.settings.keys.IKey}s to this
     * {@link de.omnikryptec.util.settings.keys.KeyContainer}
     *
     * @param ikeys {@link de.omnikryptec.util.settings.keys.IKey}s to be added
     *
     * @return A reference to this
     *         {@link de.omnikryptec.util.settings.keys.KeyContainer}
     */
    public KeyContainer addIKeys(final Collection<IKey> ikeys) {
        Util.ensureNonNull(ikeys);
        ikeys.forEach(this::addIKey);
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
        Util.ensureNonNull(name);
        this.ikeys.remove(name);
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
        this.ikeys.remove(key.getName());
        return this;
    }
    
    /**
     * Creates and adds a {@link de.omnikryptec.util.settings.keys.Key}
     *
     * @param name    Name of the new {@link de.omnikryptec.util.settings.keys.Key}
     * @param keyCode KeyCode of the new
     *                {@link de.omnikryptec.util.settings.keys.Key}
     *               
     * @return A reference to this
     *         {@link de.omnikryptec.util.settings.keys.KeyContainer}
     */
    public KeyContainer addKey(final String name, final int keyCode) {
        return addIKey(new Key(name, keyCode));
    }
    
    /**
     * Creates and adds a {@link de.omnikryptec.util.settings.keys.Key}
     *
     * @param name          Name of the new
     *                      {@link de.omnikryptec.util.settings.keys.Key}
     * @param keyCode       KeyCode of the new
     *                      {@link de.omnikryptec.util.settings.keys.Key}
     * @param isKeyboardKey sets if the new
     *                      {@link de.omnikryptec.util.settings.keys.Key} should be
     *                      a keyboard key
     *                     
     * @return A reference to this
     *         {@link de.omnikryptec.util.settings.keys.KeyContainer}
     */
    public KeyContainer addKey(final String name, final int keyCode, final boolean isKeyboardKey) {
        return addIKey(new Key(name, keyCode, isKeyboardKey));
    }
    
    //TODO Panzer1119 Create javadoc
    
    public <T extends IKey> T getIKey(final String name) {
        return getIKey(name, null);
    }
    
    public <T extends IKey> T getIKey(final String name, final T defaultValue) {
        final IKey key = this.ikeys.get(name);
        return key == null ? defaultValue : (T) key;
    }
    
    public Key getKey(final String name) {
        return (Key) this.ikeys.get(name);
    }
    
    public List<Key> getKeys() {
        return this.ikeys.values().stream().filter((key) -> key instanceof Key).map((key) -> (Key) key)
                .collect(Collectors.toList());
    }
    
    public List<Key> getKeys(final boolean isKeyboardKey) {
        return this.ikeys.values().stream().filter((key) -> key instanceof Key).map((key) -> (Key) key)
                .filter((key) -> key.isKeyboardKey() == isKeyboardKey).collect(Collectors.toList());
    }
    
    public KeyGroup getKeyGroup(final String name) {
        return (KeyGroup) this.ikeys.get(name);
    }
    
    public List<KeyGroup> getKeyGroups() {
        return this.ikeys.values().stream().filter((key) -> key instanceof KeyGroup).map((key) -> (KeyGroup) key)
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
        return this.ikeys.isEmpty();
    }
    
    /**
     * Returns <tt>true</tt> if this
     * {@link de.omnikryptec.util.settings.keys.KeyContainer} is not empty
     *
     * @return <tt>true</tt> if this
     *         {@link de.omnikryptec.util.settings.keys.KeyContainer} is not empty
     */
    public boolean isNotEmpty() {
        return !this.ikeys.isEmpty();
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
        return Objects.equals(this.ikeys, that.ikeys);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.ikeys);
    }
    
    @Override
    public String toString() {
        return "KeyContainer{" + "ikeys=" + this.ikeys + '}';
    }
    
}
