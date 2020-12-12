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

package de.omnikryptec.util.settings;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import de.codemakers.base.util.Require;
import de.codemakers.base.util.tough.ToughFunction;
import de.omnikryptec.util.Util;

public class Settings<K> {
    
    final Map<K, Object> settings;
    
    public Settings() {
        this(new ConcurrentHashMap<>());
    }
    
    public Settings(final Map<K, Object> settings) {
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
    public <T> T get(final K key) {
        Object object = this.settings.get(key);
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
     * @param key   {@link K} Key
     * @param clazz Class of the value
     * @param <T>   Type of the value
     *
     * @return Value for the key
     */
    public <T> T get(final K key, final Class<T> clazz) {
        Object object = this.settings.get(key);
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
     * @param key          {@link K} Key
     * @param defaultValue Default value
     * @param <T>          Type of the value
     *
     * @return Value for the key (or the default value if null)
     */
    public <T> T getOrDefault(final K key, final T defaultValue) {
        final T t = get(key);
        return t == null ? defaultValue : t;
    }
    
    /**
     * Returns the value for the key (or the default value if null) and casts it to
     * the clazz
     *
     * @param key          {@link K} Key
     * @param defaultValue Default value
     * @param clazz        Class of the value
     * @param <T>          Type of the value
     *
     * @return Value for the key (or the default value if null)
     */
    public <T> T getOrDefault(final K key, final T defaultValue, final Class<T> clazz) {
        final T t = get(key);
        return t == null ? defaultValue : t;
    }
    
    /**
     * Sets a value for a key
     *
     * @param key   {@link K} Key
     * @param value Value to be set
     *
     * @return A reference to this {@link de.omnikryptec.util.settings.Settings}
     */
    public Settings<K> set(final K key, final Object value) {
        Util.ensureNonNull(key);
        if (value == null) {
            return remove(key);
        }
        this.settings.put(key, value);
        return this;
    }
    
    /**
     * Sets some values for some keys
     *
     * @param settings Settings map
     *
     * @return A reference to this {@link de.omnikryptec.util.settings.Settings}
     */
    public Settings<K> setAll(final Map<K, Object> settings) {
        Util.ensureNonNull(settings);
        for (final Map.Entry<K, Object> entry : settings.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
        return this;
    }
    
    /**
     * Alters an value in this {@link de.omnikryptec.util.settings.Settings}
     *
     * @param key      Key to alter
     * @param function Function which alters the value
     * @param <R>      Type of the altered value
     * @param <T>      Type of the current value
     *
     * @return Altered value
     */
    public <R, T> R update(final K key, final ToughFunction<T, R> function) {
        Util.ensureNonNull(key);
        final T value = get(key);
        final R result = function.applyWithoutException(value);
        set(key, result);
        return result;
    }
    
    /**
     * Alters an value in this {@link de.omnikryptec.util.settings.Settings}
     *
     * @param key      Key to alter
     * @param function Function which alters the value
     * @param clazz    Class of the value
     * @param <R>      Type of the altered value
     * @param <T>      Type of the current value
     *
     * @return Altered value
     */
    public <R, T> R update(final K key, final ToughFunction<T, R> function, final Class<T> clazz) {
        Util.ensureNonNull(key);
        final T value = get(key, clazz);
        final R result = function.applyWithoutException(value);
        set(key, result);
        return result;
    }
    
    /**
     * Removes a key and its value from this
     * {@link de.omnikryptec.util.settings.Settings}
     *
     * @param key {@link K} Key of the {@link java.util.Map.Entry<K,
     *            java.lang.Object>} to get removed
     *            
     * @return A reference to this {@link de.omnikryptec.util.settings.Settings}
     */
    public Settings<K> remove(final K key) {
        this.settings.remove(key);
        return this;
    }
    
    /**
     * Removes some keys and their values from this
     * {@link de.omnikryptec.util.settings.Settings}
     *
     * @param keys Keys to get removed
     *
     * @return A reference to this {@link de.omnikryptec.util.settings.Settings}
     */
    public Settings<K> removeAll(final List<K> keys) {
        Util.ensureNonNull(this.settings);
        keys.forEach(this::remove);
        return this;
    }
    
    /**
     * Removes a key and its value from this
     * {@link de.omnikryptec.util.settings.Settings} if the current value for the
     * key matches the given value
     *
     * @param key   {@link K} Key of the {@link java.util.Map.Entry<K,
     *              java.lang.Object>} to get removed
     * @param value Value to match if a {@link java.util.Map.Entry<K,
     *              java.lang.Object>} should get removed
     *              
     * @return A reference to this {@link de.omnikryptec.util.settings.Settings}
     */
    public Settings<K> remove(final K key, final Object value) {
        this.settings.remove(key, value);
        return this;
    }
    
    /**
     * Removes some keys and their values from this
     * {@link de.omnikryptec.util.settings.Settings} if the current value for they
     * key matches the given value
     *
     * @param settings Keys and values to get removed
     *
     * @return A reference to this {@link de.omnikryptec.util.settings.Settings}
     */
    public Settings<K> removeAll(final Map<K, Object> settings) {
        Util.ensureNonNull(settings);
        settings.forEach(this::remove);
        return this;
    }
    
    /**
     * Returns <tt>true</tt> if this {@link de.omnikryptec.util.settings.Settings}
     * has a specific key
     *
     * @param key {@link K} Key to be searched for
     *
     * @return <tt>true</tt> if this {@link de.omnikryptec.util.settings.Settings}
     *         contains a value for the specified key
     */
    public boolean hasKey(final K key) {
        return this.settings.containsKey(key);
    }
    
    /**
     * Returns <tt>true</tt> if this {@link de.omnikryptec.util.settings.Settings}
     * has a specific value
     *
     * @param value Value to be searched for
     *
     * @return <tt>true</tt> if this {@link de.omnikryptec.util.settings.Settings}
     *         maps one or more keys to the specified value
     */
    public boolean hasValue(final Object value) {
        return this.settings.containsValue(value);
    }
    
    /**
     * Removes all {@link java.util.Map.Entry<K, java.lang.Object>}s of this
     * {@link de.omnikryptec.util.settings.Settings}
     */
    public boolean clear() {
        this.settings.clear();
        return this.settings.isEmpty();
    }
    
    public Settings<K> copy() {
        return new Settings<K>().setAll(this.settings);
    }
    
    public void set(final Settings<K> copyable) {
        final Settings<K> settings = Require.clazz(copyable, Settings.class);
        if (settings != null) {
            Util.ensureNonNull(settings.settings);
            this.settings.clear();
            this.settings.putAll(settings.settings);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Settings)) {
            return false;
        }
        final Settings<?> settings1 = (Settings<?>) o;
        return Objects.equals(this.settings, settings1.settings);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.settings);
    }
    
    @Override
    public String toString() {
        return "Settings{" + "settings=" + this.settings + '}';
    }
    
}
