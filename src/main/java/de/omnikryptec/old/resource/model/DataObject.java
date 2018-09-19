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

package de.omnikryptec.old.resource.model;

import org.lwjgl.opengl.GL11;

public class DataObject {

	private int[] intdata;
	private float[] floatdata;

	private int type = -1;

	public DataObject(int[] intdata) {
		this.intdata = intdata;
		type = GL11.GL_INT;
	}

	public DataObject(float[] floatdata) {
		this.floatdata = floatdata;
		type = GL11.GL_FLOAT;
	}

	public boolean holdsFloat() {
		return type == GL11.GL_FLOAT;
	}

	public boolean holdsInt() {
		return type == GL11.GL_INT;
	}

	public int[] getInt() {
		return intdata;
	}

	public float[] getFloat() {
		return floatdata;
	}

	public int getLength() {
		return holdsFloat() ? floatdata.length : (holdsInt() ? intdata.length : -1);
	}

	public void store(VertexBufferObject vbo) {
		if (holdsFloat()) {
			vbo.storeData(floatdata);
		} else if (holdsInt()) {
			vbo.storeData(intdata);
		}
	}

	public int getType() {
		return type;
	}
}
