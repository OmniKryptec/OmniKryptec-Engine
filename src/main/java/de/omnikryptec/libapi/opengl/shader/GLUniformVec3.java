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

package de.omnikryptec.libapi.opengl.shader;

import org.lwjgl.opengl.GL20;

import de.omnikryptec.libapi.exposed.render.shader.UniformVec3;

public class GLUniformVec3 extends GLUniform implements UniformVec3 {
    private final float[] old = new float[3];
    private boolean used = false;
    
    public GLUniformVec3(final String name) {
        super(name);
    }
    
    @Override
    public void loadVec3(final float x, final float y, final float z) {
        if (existsInCompilation() && (!this.used || x != this.old[0] || y != this.old[1] || z != this.old[2])) {
            GL20.glUniform3f(super.getLocation(), x, y, z);
            this.old[0] = x;
            this.old[1] = y;
            this.old[2] = z;
            this.used = true;
        }
    }
    
}
