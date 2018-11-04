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

package de.omnikryptec.util.settings;

import de.codemakers.base.util.Require;
import de.codemakers.base.util.interfaces.Copyable;
import de.omnikryptec.util.Util;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Settings<K> implements Copyable {
    
    final Map<K, Object> settings;
    
    public Settings() {
        this(new ConcurrentHashMap<>());
    }
    
    public Settings(Map<K, Object> settings) {
        this.settings = settings;
    }
    
    /**
     * Returns the value for the key
     *
     * @param key {@link K} Key
     * @param <T> Type of the value
     *
     * @return Value for the key
     */
    public <T> T get(K key) {
        Object object = settings.get(key);
        if (object == null) {
            if (key instanceof Defaultable) {
                object = ((Defaultable) key).getDefault();
            }
        }
        return (T) object;
    }
    
    /**
     * Returns the value for the key and casts it to the clazz
     *
     * @param key {@link K} Key
     * @param clazz Class of the Value
     * @param <T> Type of the value
     *
     * @return Value for the key
     */
    public <T> T get(K key, T clazz) {
        Object object = settings.get(key);
        if (object == null) {
            if (key instanceof Defaultable) {
                object = ((Defaultable) key).getDefault();
            }
        }
        return (T) object;
    }
    
    /**
     * Returns the value for the key (or the default value if null)
     *
     * @param key {@link K} Key
     * @param defaultValue Default value
     * @param <T> Type of the value
     *
     * @return Value for the key (or the default value if null)
     */
    public <T> T getOrDefault(K key, T defaultValue) {
        final T t = get(key);
        return t == null ? defaultValue : t;
    }
    
    /**
     * Returns the value for the key (or the default value if null) and casts it to the clazz
     *
     * @param key {@link K} Key
     * @param defaultValue Default value
     * @param clazz Class of the Value
     * @param <T> Type of the value
     *
     * @return Value for the key (or the default value if null)
     */
    public <T> T getOrDefault(K key, T defaultValue, T clazz) {
        final T t = get(key);
        return t == null ? defaultValue : t;
    }
    
    /**
     * Sets a value for a key
     *
     * @param key {@link K} Key
     * @param value Value to be set
     *
     * @return A reference to this {@link de.omnikryptec.util.settings.Settings}
     */
    public Settings<K> set(K key, Object value) {
        settings.put(key, value);
        return this;
    }
    
    /**
     * Sets some values for some keys
     *
     * @param settings Settings map
     *
     * @return A reference to this {@link de.omnikryptec.util.settings.Settings}
     */
    public Settings<K> setAll(Map<K, Object> settings) {
        Util.ensureNonNull(settings);
        this.settings.putAll(settings);
        return this;
    }
    
    /**
     * Removes a key and its value from this
     * {@link de.omnikryptec.util.settings.Settings}
     *
     * @param key {@link K} Key of the {@link java.util.Map.Entry<K,
     * java.lang.Object>} to get removed
     *
     * @return A reference to this {@link de.omnikryptec.util.settings.Settings}
     */
    public Settings<K> remove(K key) {
        settings.remove(key);
        return this;
    }
    
    /**
     * Removes a key and its value from this
     * {@link de.omnikryptec.util.settings.Settings} if the value for the key
     * matches the given value
     *
     * @param key {@link K} Key of the {@link java.util.Map.Entry<K,
     * java.lang.Object>} to get removed
     * @param value Value to match if a {@link java.util.Map.Entry<K,
     * java.lang.Object>} should get removed
     *
     * @return A reference to this {@link de.omnikryptec.util.settings.Settings}
     */
    public Settings<K> remove(K key, Object value) {
        settings.remove(key, value);
        return this;
    }
    
    /**
     * Returns <tt>true</tt> if this {@link de.omnikryptec.util.settings.Settings}
     * has a specific key
     *
     * @param key {@link K} Key to be searched for
     *
     * @return <tt>true</tt> if this {@link de.omnikryptec.util.settings.Settings}
     * contains a value for the specified key
     */
    public boolean hasKey(K key) {
        return settings.containsKey(key);
    }
    
    /**
     * Returns <tt>true</tt> if this {@link de.omnikryptec.util.settings.Settings}
     * has a specific value
     *
     * @param value Value to be searched for
     *
     * @return <tt>true</tt> if this {@link de.omnikryptec.util.settings.Settings}
     * maps one or more keys to the specified value
     */
    public boolean hasValue(Object value) {
        return settings.containsValue(value);
    }
    
    /**
     * Removes all {@link java.util.Map.Entry<K, java.lang.Object>}s of this
     * {@link de.omnikryptec.util.settings.Settings}
     */
    public boolean clear() {
        settings.clear();
        return settings.isEmpty();
    }
    
    @Override
    public Settings<K> copy() {
        return new Settings<K>().setAll(settings);
    }
    
    @Override
    public void set(Copyable copyable) {
        final Settings<K> settings = Require.clazz(copyable, Settings.class);
        if (settings != null) {
            Util.ensureNonNull(settings.settings);
            this.settings.clear();
            this.settings.putAll(settings.settings);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !getClass().isAssignableFrom(o.getClass())) {
            return false;
        }
        final Settings<?> settings1 = (Settings<?>) o;
        return Objects.equals(settings, settings1.settings);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(settings);
    }
    
    @Override
    public String toString() {
        return "Settings{" + "settings=" + settings + '}';
    }
    
}
