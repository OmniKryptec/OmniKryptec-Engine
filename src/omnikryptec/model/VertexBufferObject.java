package omnikryptec.model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

public class VertexBufferObject {

	private final int vboId;
	private final int type;

	private VertexBufferObject(int vboId, int type) {
		this.vboId = vboId;
		this.type = type;
	}

	public static VertexBufferObject create(int type) {
		int id = GL15.glGenBuffers();
		return new VertexBufferObject(id, type);
	}

	public static VertexBufferObject createEmpty(int type, int floatCount){
		VertexBufferObject vbo = create(type);
		vbo.bind();
		GL15.glBufferData(vbo.type, floatCount * 4, GL15.GL_STREAM_DRAW);
		return vbo;
	}
	
	public void bind() {
		GL15.glBindBuffer(type, vboId);
	}

	public void unbind() {
		GL15.glBindBuffer(type, 0);
	}

	public void storeData(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		storeData(buffer);
	}

	public void storeData(FloatBuffer data) {
		GL15.glBufferData(type, data, GL15.GL_STATIC_DRAW);
	}

	public void storeData(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		storeData(buffer);
	}

	public void storeData(IntBuffer data) {
		GL15.glBufferData(type, data, GL15.GL_STATIC_DRAW);
	}

	public void updateData(float[] data, FloatBuffer buffer){
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		bind();
		GL15.glBufferData(type, buffer.capacity() * 4, GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(type, 0, buffer);
		unbind();
	}
	
	public void addInstancedAttribute(VertexArrayObject vao, int attributNr, int dataSize, int instancedDataSize, int offset) {
		bind();
		vao.bind();
		GL20.glVertexAttribPointer(attributNr, dataSize, GL11.GL_FLOAT, false, instancedDataSize * 4, offset * 4);
		GL33.glVertexAttribDivisor(attributNr, 1);
		unbind();
		vao.unbind();
	}
	
	public void delete() {
		GL15.glDeleteBuffers(vboId);
	}

}
