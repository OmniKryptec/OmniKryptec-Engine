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

package de.omnikryptec.util.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

//Surprisingly faster than a guava Multiset
public class CountingMap<T> implements Iterable<T> {
    
    private final Map<T, Long> map = new HashMap<>();
    private boolean retainZeros = false;
    
    public CountingMap() {
        this(false);
    }
    
    public CountingMap(final boolean retainZeros) {
        this.retainZeros = retainZeros;
    }
    
    public long get(final T t) {
        return this.map.containsKey(t) ? this.map.get(t) : 0;
    }
    
    public long increment(final T t) {
        return increment(t, 1);
    }
    
    public long increment(final T t, final long amount) {
        long l;
        this.map.put(t, l = get(t) + amount);
        return l;
    }
    
    public long decrement(final T t) {
        return decrement(t, 1);
    }
    
    public long decrement(final T t, final long amount) {
        long l;
        this.map.put(t, l = get(t) - amount);
        if (!this.retainZeros && l == 0) {
            remove(t);
        }
        return l;
    }
    
    public long remove(final T t) {
        return this.map.remove(t);
    }
    
    public Set<T> keySet() {
        return this.map.keySet();
    }
    
    @Override
    public Iterator<T> iterator() {
        return this.map.keySet().iterator();
    }
    
}
