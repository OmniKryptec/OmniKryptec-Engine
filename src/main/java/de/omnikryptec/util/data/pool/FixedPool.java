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

package de.omnikryptec.util.data.pool;

import de.omnikryptec.util.data.FixedStack;

/**
 * A {@link Pool} fixed in size. More instances than the ones that are free can
 * be retrieved. If this Pool is full and an instance is freed, it will not be
 * added to this Pool.
 *
 * @param <T> the Type of the Object being pooled.
 *
 * @author pcfreak9000
 */
public abstract class FixedPool<T> extends Pool<T> {
    
    private final FixedStack<T> free;
    private boolean poolable = false;
    
    public FixedPool(final Class<T> clazz, final int size, final boolean prewarm) {
        this.poolable = Poolable.class.isAssignableFrom(clazz);
        this.free = new FixedStack<>(size);
        if (prewarm) {
            for (int i = 0; i < size; i++) {
                this.free.push(newObject());
            }
        }
    }
    
    @Override
    public T retrieve() {
        return this.free.isEmpty() ? newObject() : this.free.pop();
    }
    
    @Override
    public void free(final T t) {
        if (this.poolable) {
            ((Poolable) t).reset();
        }
        if (!this.free.isFull()) {
            this.free.push(t);
        }
    }
    
    @Override
    public int available() {
        return Math.max(1, this.free.filled());
    }
}
