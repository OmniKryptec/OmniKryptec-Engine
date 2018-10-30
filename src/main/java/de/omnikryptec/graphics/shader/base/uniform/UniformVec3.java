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

import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

import de.omnikryptec.util.data.Color;

public class UniformVec3 extends Uniform {
    private float currentX;
    private float currentY;
    private float currentZ;
    private boolean used = false;

    public UniformVec3(String name) {
	super(name);
    }

    public void loadVec3(Vector3f vector) {
	if (vector != null) {
	    loadVec3(vector.x, vector.y, vector.z);
	}
    }

    public void loadVec3(float[] array) {
	loadVec3(array[0], array[1], array[2]);
    }

    public void loadVec3(float x, float y, float z) {
	if (isFound() && (!used || x != currentX || y != currentY || z != currentZ)) {
	    this.currentX = x;
	    this.currentY = y;
	    this.currentZ = z;
	    used = true;
	    GL20.glUniform3f(super.getLocation(), x, y, z);
	}
    }

    public void loadColor(Color color) {
	loadVec3(color.getR(), color.getG(), color.getB());
    }

}
