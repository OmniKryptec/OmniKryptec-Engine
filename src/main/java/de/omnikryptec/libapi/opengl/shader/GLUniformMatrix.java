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

package de.omnikryptec.libapi.opengl.shader;

import java.nio.FloatBuffer;

import org.joml.Matrix4fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import de.omnikryptec.libapi.exposed.render.shader.UniformMatrix;

public class GLUniformMatrix extends GLUniform implements UniformMatrix {
    
    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
    
    public GLUniformMatrix(final String name) {
        super(name);
    }
    
    @Override
    public void loadMatrix(final Matrix4fc matrix) {
        if (existsInCompilation()) {
            matrix.get(matrixBuffer);
            matrixBuffer.position(16);
            matrixBuffer.flip();
            GL20.glUniformMatrix4fv(super.getLocation(), false, matrixBuffer);
        }
    }
    
}
