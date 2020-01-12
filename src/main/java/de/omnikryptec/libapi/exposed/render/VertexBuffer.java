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

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import de.omnikryptec.libapi.exposed.render.RenderAPI.BufferUsage;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;

public interface VertexBuffer {

    /**
     * Binds this {@link VertexBuffer}.
     *
     * @see VertexArray#bindArray()
     */
    void bindBuffer();

    /**
     * Unbinds this {@link VertexBuffer}
     */
    void unbindBuffer();

    /**
     * Stores data in this {@link VertexBuffer}. This VertexBuffer will be
     * auto-bound and the supplied {@link FloatBuffer} will be flipped by this
     * method.
     *
     * @param data the float-data to be stored
     */
    void updateData(FloatBuffer data);

    /**
     * Stores data in this {@link VertexBuffer}. This VertexBuffer will be
     * auto-bound and the supplied {@link IntBuffer} will be flipped by this method.
     *
     * @param data the int-data to be stored
     */
    void updateData(IntBuffer data);

    /**
     * Stores data in this {@link VertexBuffer}. This method constructs and fills a
     * {@link FloatBuffer} with the supplied float[]. The data is then stored with
     * {@link #updateData(FloatBuffer)}
     *
     * @param data the float-data to be stored
     */
    default void updateData(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        updateData(buffer);
    }

    /**
     * Stores data in this {@link VertexBuffer}. This method constructs and fills an
     * {@link IntBuffer} with the supplied int[]. The data is then stored with
     * {@link #updateData(IntBuffer}
     *
     * @param data the int-data to be stored
     */
    default void updateData(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        updateData(buffer);
    }

    /**
     * Initializes this {@link VertexBuffer}.
     *
     * @param usage use case
     * @param type  the type of what is going to be stored in this
     *              {@link VertexBuffer}. Using a wrong type can in general result
     *              in unexpected behaviour.
     * @param size  the amount of max entries
     */
    void setDescription(BufferUsage usage, Type type, int size);

    /**
     * The size of this buffer in amount of entries (not in bytes)
     *
     * @return size
     */
    int size();
}
