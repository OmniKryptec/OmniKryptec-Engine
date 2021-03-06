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

package de.omnikryptec.libapi.exposed.render.shader;

import org.joml.Matrix4f;

import de.omnikryptec.libapi.opengl.shader.GLUniformMatrix;

/**
 * UniformMatrixArray
 *
 * @author Panzer1119
 */
public class UniformMatrixArray {

    private final GLUniformMatrix[] uniformMatrices;
    
    public UniformMatrixArray(final String name, final int size) {
        this.uniformMatrices = new GLUniformMatrix[size];
        for (int i = 0; i < size; i++) {
            this.uniformMatrices[i] = new GLUniformMatrix(name + "[" + i + "]");
        }
    }
    
    public final UniformMatrixArray loadMatrixArray(final Matrix4f[] matrices) {
        for (int i = 0; i < matrices.length; i++) {
            this.uniformMatrices[i].loadMatrix(matrices[i]);
        }
        return this;
    }
    
}
