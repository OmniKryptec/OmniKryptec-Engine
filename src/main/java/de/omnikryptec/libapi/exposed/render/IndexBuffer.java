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
    
    void updateData(IntBuffer data);
    
    default void updateData(final int[] data) {
        final IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        updateData(buffer);
    }
    
    /**
     * Initializes this {@link IndexBuffer}.
     *
     * @param usage use case
     * @param type  the type of what is going to be stored in this
     *              {@link IndexBuffer}. Using a wrong type can in general result in
     *              unexpected behaviour.
     * @param size  the amount of max entries
     */
    void setDescription(BufferUsage usage, int size);

    /**
     * the size of this buffer
     *
     * @return size
     */
    int size();

}
