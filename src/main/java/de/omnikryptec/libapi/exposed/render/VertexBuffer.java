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
    
    void bindBuffer();
    
    void unbindBuffer();
    
    void storeData(FloatBuffer data, boolean dynamic, int size);
    
    default void storeData(final float[] data, final boolean dynamic) {
        final FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        storeData(buffer, dynamic, data.length);
    }
    
    void storeData(IntBuffer data, boolean dynamic, int size);
    
    default void storeData(final int[] data, final boolean dynamic) {
        final IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        storeData(buffer, dynamic, data.length);
    }

    int size();
}
