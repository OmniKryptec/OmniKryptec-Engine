/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;

import de.omnikryptec.util.data.Color;

public class UniformVec4 extends Uniform {

    private final float[] old = new float[4];
    private boolean used = false;

    public UniformVec4(final String name) {
        super(name);
    }

    public void loadVec4(final Vector4f vector) {
        loadVec4(vector.x, vector.y, vector.z, vector.w);
    }

    public void loadVec4(final float[] array) {
        loadVec4(array[0], array[1], array[2], array[3]);
    }

    public void loadVec4(final float x, final float y, final float z, final float w) {
        if (isFound() && (!this.used || x != this.old[0] || y != this.old[1] || z != this.old[2] || w != this.old[3])) {
            GL20.glUniform4f(super.getLocation(), x, y, z, w);
            this.old[0] = x;
            this.old[1] = y;
            this.old[2] = z;
            this.old[3] = w;
            this.used = true;
        }
    }

    public void loadColor(final Color color) {
        loadVec4(color.getR(), color.getG(), color.getB(), color.getA());
    }

}
