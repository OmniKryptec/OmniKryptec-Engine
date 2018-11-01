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

package de.omnikryptec.util.data.settings;

import java.util.HashMap;

public class Settings<K> {
    
    private final HashMap<K, Object> settings_objects = new HashMap<>();
    
    public <T> T get(K key) {
        Object obj = settings_objects.get(key);
        if (obj == null) {
            if (key instanceof Defaultable) {
                obj = ((Defaultable) key).getDefault();
            }
        }
        return (T) obj;
    }
    
    public <T> T getOrDefault(K key, T def) {
        T t = get(key);
        return t == null ? def : t;
    }
    
    public Settings<K> set(K key, Object value) {
        settings_objects.put(key, value);
        return this;
    }
    
    public boolean has(K key) {
        return settings_objects.containsKey(key);
    }
    
}
