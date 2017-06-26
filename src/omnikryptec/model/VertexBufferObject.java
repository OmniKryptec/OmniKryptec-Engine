package omnikryptec.model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;

import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;

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
		int id = GL15.glGenBuffers();
		return new VertexBufferObject(id, type);
	}

	public static VertexBufferObject createEmpty(int type, int floatCount) {
		VertexBufferObject vbo = create(type);
		vbo.bind();
		GL15.glBufferData(vbo.type, floatCount * 4, GL15.GL_STREAM_DRAW);
		return vbo;
	}

	public void bind() {
		GL15.glBindBuffer(type, vboId);
	}

	@Deprecated
	public void unbind() {
		GL15.glBindBuffer(type, 0);
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
		GL15.glBufferData(type, data, GL15.GL_STATIC_DRAW);
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
		GL15.glBufferData(type, data, GL15.GL_STATIC_DRAW);
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
		GL15.glBufferData(type, buffer.capacity() * 4, GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(type, 0, buffer);
	}

	public void addInstancedAttribute(VertexArrayObject vao, int attributNr, int dataSize, int instancedDataSize,
			int offset) {
		addInstancedAttribute(vao, attributNr, dataSize, instancedDataSize, offset, 1);
	}
	
	public void addInstancedAttribute(VertexArrayObject vao, int attributNr, int dataSize, int instancedDataSize,
			int offset, int divisor) {
		bind();
		vao.bind();
		GL20.glVertexAttribPointer(attributNr, dataSize, GL11.GL_FLOAT, false, instancedDataSize * 4, offset * 4);
		GL33.glVertexAttribDivisor(attributNr, 1);
	}

	public void delete() {
		GL15.glDeleteBuffers(vboId);
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
