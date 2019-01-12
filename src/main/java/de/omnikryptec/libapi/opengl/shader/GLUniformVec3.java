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

import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

import de.omnikryptec.util.data.Color;

public class GLUniformVec3 extends GLUniform {
    private float currentX;
    private float currentY;
    private float currentZ;
    private boolean used = false;
    
    public GLUniformVec3(final String name) {
        super(name);
    }
    
    public void loadVec3(final Vector3f vector) {
        if (vector != null) {
            loadVec3(vector.x, vector.y, vector.z);
        }
    }
    
    public void loadVec3(final float[] array) {
        loadVec3(array[0], array[1], array[2]);
    }
    
    public void loadVec3(final float x, final float y, final float z) {
        if (isFound() && (!this.used || x != this.currentX || y != this.currentY || z != this.currentZ)) {
            this.currentX = x;
            this.currentY = y;
            this.currentZ = z;
            this.used = true;
            GL20.glUniform3f(super.getLocation(), x, y, z);
        }
    }
    
    public void loadColor(final Color color) {
        loadVec3(color.getR(), color.getG(), color.getB());
    }
    
}
