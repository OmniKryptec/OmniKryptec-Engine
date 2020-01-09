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

package de.omnikryptec.old.resource.model;

import de.omnikryptec.old.graphics.OpenGL;
import de.omnikryptec.old.util.logger.LogLevel;
import de.omnikryptec.old.util.logger.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VertexBufferObject {

    private final int vboId;
    private final int type;

    private static final List<VertexBufferObject> active = new ArrayList<>();

    private VertexBufferObject(int vboId, int type) {
	this.vboId = vboId;
	this.type = type;
	active.add(this);
    }

    public static VertexBufferObject create(int type) {
	int id = OpenGL.gl15genBuffers();
	return new VertexBufferObject(id, type);
    }

    public void bind() {
	OpenGL.gl15bindBuffer(type, vboId);
    }

    @Deprecated
    public void unbind() {
	OpenGL.gl15bindBuffer(type, 0);
    }

    public void storeData(float[] data) {
	FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
	buffer.put(data);
	buffer.flip();
	storeData(buffer);
    }

    public void storeData(Float[] data) {
	storeData(dc(data));
    }

    public void storeData(FloatBuffer data) {
	OpenGL.gl15bufferData(type, data, GL15.GL_STATIC_DRAW);
    }

    public void storeData(int[] data) {
	IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
	buffer.put(data);
	buffer.flip();
	storeData(buffer);
    }

    public void storeData(Integer[] data) {
	storeData(dc(data));
    }

    public void storeData(IntBuffer data) {
	OpenGL.gl15bufferData(type, data, GL15.GL_STATIC_DRAW);
    }

    public void updateData(float[] data, FloatBuffer buffer) {
	buffer.clear();
	if (data.length > buffer.capacity()) {
	    data = Arrays.copyOf(data, buffer.capacity());
	    if (Logger.isDebugMode()) {
		Logger.log("BufferOverflow", LogLevel.WARNING);
	    }
	}
	buffer.put(data);
	buffer.flip();
	bind();
	OpenGL.gl15bufferData(type, buffer.capacity() * 4, GL15.GL_DYNAMIC_DRAW);
	OpenGL.gl15bufferSubData(type, 0, buffer);
    }

    public void addInstancedAttribute(VertexArrayObject vao, int attributNr, int dataSize, int instancedDataSize,
	    int offset) {
	addInstancedAttribute(vao, attributNr, dataSize, instancedDataSize, offset, 1);
    }

    public void addInstancedAttribute(VertexArrayObject vao, int attributNr, int dataSize, int instancedDataSize,
	    int offset, int divisor) {
	bind();
	vao.bind();
	OpenGL.gl20vertexAttribPointer(attributNr, dataSize, GL11.GL_FLOAT, false, instancedDataSize * 4, offset * 4);
	OpenGL.gl33vertexAttribDivisor(attributNr, divisor);
    }

    public void delete() {
	OpenGL.gl15deleteBuffers(vboId);
    }

    private static float[] dc(Float[] is) {
	float[] newa = new float[is.length];
	for (int i = 0; i < is.length; i++) {
	    newa[i] = is[i].floatValue();
	}
	return newa;
    }

    private static int[] dc(Integer[] is) {
	int[] newa = new int[is.length];
	for (int i = 0; i < is.length; i++) {
	    newa[i] = is[i].intValue();
	}
	return newa;
    }

    public static void cleanup() {
	for (int i = 0; i < active.size(); i++) {
	    active.get(i).delete();
	}
    }
}
