package omnikryptec.model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

public class VertexBufferObject {
	
	private final int vboId;
	private final int type;
	
	private VertexBufferObject(int vboId, int type){
		this.vboId = vboId;
		this.type = type;
	}
	
	public static VertexBufferObject create(int type){
		int id = GL15.glGenBuffers();
		return new VertexBufferObject(id, type);
	}
	
	public void bind(){
		GL15.glBindBuffer(type, vboId);
	}
	
	public void unbind(){
		GL15.glBindBuffer(type, 0);
	}
	
	public void storeData(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		storeData(buffer);
	}
	
	public void storeData(FloatBuffer data){
		GL15.glBufferData(type, data, GL15.GL_STATIC_DRAW);
	}
	
	public void storeData(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		storeData(buffer);
	}
	
	public void storeData(IntBuffer data){
		GL15.glBufferData(type, data, GL15.GL_STATIC_DRAW);
	}
	
	public void delete(){
		GL15.glDeleteBuffers(vboId);
	}

}
