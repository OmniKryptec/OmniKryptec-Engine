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

package de.omnikryptec.renderer.d3;

import java.lang.reflect.Array;
import java.util.HashMap;

public class RenderMap<K, V> {

    private Class<K> keyclass;
    private HashMap<K, V> map = new HashMap<>(RenderChunk3D.DEFAULT_CAPACITY, 0.7f);
    private K[] keys;

    private boolean keysDirty = true;

    public RenderMap(Class<K> keyclass) {
        this.keyclass = keyclass;
    }

    public void put(K k, V v) {
        map.put(k, v);
        keysDirty = true;
    }

    public V get(K key) {
        return map.get(key);
    }

    
    public K[] keysArray() {
        if (keysDirty) {
            keys = map.keySet().toArray((K[]) Array.newInstance(keyclass, map.size()));
            keysDirty = false;
        }
        return keys;
    }

    public void remove(K tm) {
        map.remove(tm);
        keysDirty = true;
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

	public int size() {
		return map.size();
	}

	public void clear() {
		map.clear();
		keysDirty = true;
	}

}
