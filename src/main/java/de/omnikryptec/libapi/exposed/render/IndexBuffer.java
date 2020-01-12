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

package de.omnikryptec.libapi.exposed.render;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import de.omnikryptec.libapi.exposed.render.RenderAPI.BufferUsage;

public interface IndexBuffer {
    
    /**
     * Binds this {@link IndexBuffer}.
     *
     * @see VertexArray#bindArray()
     */
    void bindBuffer();
    
    /**
     * Unbinds this {@link IndexBuffer}
     */
    void unbindBuffer();
    
    /**
     * Stores indices in this {@link IndexBuffer}. This {@link IndexBuffer} will be
     * auto-bound and the supplied {@link IntBuffer} will be flipped by this method.
     *
     * @param data  the indices to be stored
     * @param usage buffer usage
     * @param size  the size of the added data
     */
    void storeData(IntBuffer data, BufferUsage usage, int size);
    
    /**
     * Stores indices in this {@link IndexBuffer}. This method constructs and fills
     * an {@link IntBuffer} with the supplied int[]. The data is then stored with
     * {@link #storeData(IntBuffer, boolean, int)}
     *
     * @param data  the indices to be stored
     * @param usage buffer usage
     */
    default void storeData(final int[] data, final BufferUsage usage) {
        final IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        storeData(buffer, usage, data.length);
    }
    
    /**
     * the size of this buffer
     *
     * @return size
     */
    int size();
    
}
