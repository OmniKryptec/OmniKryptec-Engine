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

package de.omnikryptec.libapi.opengl.buffer;

import java.nio.IntBuffer;

import org.lwjgl.opengl.GL15;

import de.omnikryptec.libapi.exposed.render.IndexBuffer;

public class GLIndexBuffer extends GLBuffer implements IndexBuffer {
    
    private int size;
    
    public GLIndexBuffer() {
        super(GL15.GL_ELEMENT_ARRAY_BUFFER);
    }
    
    @Override
    public void storeData(final IntBuffer data, final boolean dynamic, final int size) {
        this.size = size;
        data.flip();
        bindBuffer();
        GL15.glBufferData(bufferType(), data, dynamic ? GL15.GL_DYNAMIC_DRAW : GL15.GL_STATIC_DRAW);
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
}
