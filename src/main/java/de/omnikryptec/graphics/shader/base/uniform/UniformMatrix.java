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

package de.omnikryptec.graphics.shader.base.uniform;

import org.joml.Matrix4fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;

public class UniformMatrix extends Uniform {
    
    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
    
    public UniformMatrix(final String name) {
        super(name);
    }
    
    public void loadMatrix(final Matrix4fc matrix) {
        if (isFound()) {
            matrixBuffer.put(matrix.m00());
            matrixBuffer.put(matrix.m01());
            matrixBuffer.put(matrix.m02());
            matrixBuffer.put(matrix.m03());
            matrixBuffer.put(matrix.m10());
            matrixBuffer.put(matrix.m11());
            matrixBuffer.put(matrix.m12());
            matrixBuffer.put(matrix.m13());
            matrixBuffer.put(matrix.m20());
            matrixBuffer.put(matrix.m21());
            matrixBuffer.put(matrix.m22());
            matrixBuffer.put(matrix.m23());
            matrixBuffer.put(matrix.m30());
            matrixBuffer.put(matrix.m31());
            matrixBuffer.put(matrix.m32());
            matrixBuffer.put(matrix.m33());
            // funktioniert nicht
            // matrixBuffer = matrix.get(matrixBuffer);
            matrixBuffer.flip();
            GL20.glUniformMatrix4fv(super.getLocation(), false, matrixBuffer);
        }
    }
    
}
