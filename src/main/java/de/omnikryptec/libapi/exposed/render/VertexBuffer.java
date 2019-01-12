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

package de.omnikryptec.libapi.exposed.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

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
     * @param data    the float-data to be stored
     * @param dynamic buffer usage
     * @param size    the size of the added data
     */
    void storeData(FloatBuffer data, boolean dynamic, int size);

    /**
     * Stores data in this {@link VertexBuffer}. This method constructs and fills a
     * {@link FloatBuffer} with the supplied float[]. The data is then stored with
     * {@link #storeData(IntBuffer, boolean, int)}
     *
     * @param data    the float-data to be stored
     * @param dynamic buffer usage
     */
    default void storeData(final float[] data, final boolean dynamic) {
        final FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        storeData(buffer, dynamic, data.length);
    }

    /**
     * Stores data in this {@link VertexBuffer}. This VertexBuffer will be
     * auto-bound and the supplied {@link IntBuffer} will be flipped by this method.
     *
     * @param data    the int-data to be stored
     * @param dynamic buffer usage
     * @param size    the size of the added data
     */
    void storeData(IntBuffer data, boolean dynamic, int size);

    /**
     * Stores data in this {@link VertexBuffer}. This method constructs and fills an
     * {@link IntBuffer} with the supplied int[]. The data is then stored with
     * {@link #storeData(IntBuffer, boolean, int)}
     *
     * @param data    the int-data to be stored
     * @param dynamic buffer usage
     */
    default void storeData(final int[] data, final boolean dynamic) {
        final IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        storeData(buffer, dynamic, data.length);
    }

    /**
     * The size of this buffer
     *
     * @return size
     */
    int size();
}
