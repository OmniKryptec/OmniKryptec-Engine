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

package de.omnikryptec.libapi.opengl.buffer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

import de.omnikryptec.libapi.exposed.render.RenderAPI.BufferUsage;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.opengl.OpenGLUtil;

public class GLShaderStorageBuffer extends GLBuffer {

    public GLShaderStorageBuffer() {
        super(GL43.GL_SHADER_STORAGE_BUFFER);
    }

    public void setDescription(BufferUsage usage, Type type, int size, int index) {
        bindBuffer();
        GL15.glBufferData(bufferType(), size * OpenGLUtil.sizeof(type), OpenGLUtil.bufferUsageId(usage));
        GL30.glBindBufferBase(bufferType(), index, bufferId());
    }

    public void updateData(IntBuffer data) {
        data.flip();
        bindBuffer();
        GL15.glBufferSubData(bufferType(), 0, data);
    }

    public void updateData(FloatBuffer data) {
        data.flip();
        bindBuffer();
        GL15.glBufferSubData(bufferType(), 0, data);
    }
}
