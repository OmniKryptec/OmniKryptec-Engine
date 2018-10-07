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

package de.omnikryptec.graphics.shader.base;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;

import de.omnikryptec.util.data.Color;

public class UniformVec4 extends Uniform {

	private float[] old = new float[4];
	private boolean used = false;

	public UniformVec4(String name) {
		super(name);
	}

	public void loadVec4(Vector4f vector) {
		loadVec4(vector.x, vector.y, vector.z, vector.w);
	}

	public void loadVec4(float[] array) {
		loadVec4(array[0], array[1], array[2], array[3]);
	}

	public void loadVec4(float x, float y, float z, float w) {
		if (isFound() && (!used || x != old[0] || y != old[1] || z != old[2] || w != old[3])) {
			GL20.glUniform4f(super.getLocation(), x, y, z, w);
			old[0] = x;
			old[1] = y;
			old[2] = z;
			old[3] = w;
			used = true;
		}
	}
	
	public void loadColor(Color color) {
		loadVec4(color.getR(), color.getG(), color.getB(), color.getA());
	}

}
