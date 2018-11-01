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

/**
 * Collection of Objects to reuse multiple times. The objects can, but do not
 * neccasserily have to, implement {@link Poolable}.
 * 
 * @author pcfreak9000
 *
 * @param <T> the Type of the Object being pooled.
 */
public abstract class Pool<T> {

    /**
     * Get a free object. If none is available, a new one might be created via
     * {@link #newObject()} or an exception might be thrown.
     * 
     * @return a fresh-to-use instance
     * @throws NoFreeInstanceException if no free instance is available and this
     *                                 {@link Pool} does not permit new instances
     *                                 on-the-fly.
     */
    public abstract T retrieve() throws NoFreeInstanceException;

    /**
     * Create a new object for this {@link Pool}. Does not neccassarily add it to
     * this {@link Pool}.
     * 
     * @return a new instance
     */
    protected abstract T newObject();

    /**
     * Return a used instance to this {@link Pool}. If {@code T} implements
     * {@link Poolable}, it must be {@link Poolable#reset()} here. The freed
     * instance might be reused by {@link #retrieve()}.
     * 
     * @param the instance to free
     */
    public abstract void free(T t);

    /**
     * Returns the amount of free objects in this {@link Pool}. If there are
     * infinite free objects, a number equal to or greater than 1 must be returned.
     * 
     * @return amount of free objects
     */
    public abstract int available();

}
