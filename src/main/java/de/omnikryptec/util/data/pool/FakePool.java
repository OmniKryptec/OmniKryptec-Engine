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

package de.omnikryptec.util.data.pool;

/**
 * A fake Pool. {@link #retrieve()} will always return a newly created Object.
 * {@link #free(Object)} is unused. This might be useful for testing unpooled vs
 * pooled systems.
 *
 * @param <T> the Type of the Object being pooled.
 *
 * @author pcfreak9000
 */
public abstract class FakePool<T> extends Pool<T> {
    
    @Override
    public T retrieve() {
        return newObject();
    }
    
    /**
     * Does nothing.
     */
    @Override
    public void free(final T t) {
    }
    
    /**
     * Always returns 1
     */
    @Override
    public int available() {
        return 1;
    }
}
