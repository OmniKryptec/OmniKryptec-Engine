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

import de.omnikryptec.libapi.opengl.shader.GLUniformSampler;

/**
 * UniformMatrixArray
 *
 * @author Panzer1119
 */
public class UniformSamplerArray {
    //TODO there are better ways to deal with arrays of uniforms
    private final GLUniformSampler[] uniforms;
    
    public UniformSamplerArray(final String name, final int size) {
        this.uniforms = new GLUniformSampler[size];
        for (int i = 0; i < size; i++) {
            this.uniforms[i] = new GLUniformSampler(name + "[" + i + "]");
        }
    }
    
    public final UniformSamplerArray loadMatrixArray(final int[] ints) {
        for (int i = 0; i < uniforms.length; i++) {
            this.uniforms[i].setSampler(ints[i]);
        }
        return this;
    }
    
}
