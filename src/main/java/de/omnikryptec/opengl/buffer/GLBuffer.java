package de.omnikryptec.opengl.buffer;

import org.lwjgl.opengl.GL15;

public abstract class GLBuffer {

	private final int pointer;
	private final int type;
	
	public GLBuffer(int type) {
		this.type = type;
		this.pointer = GL15.glGenBuffers();
	}
		
	public void delete() {
		GL15.glDeleteBuffers(pointer);
	}
	
	public int id() {
		return pointer;
	}
	
	public int target() {
		return type;
	}
	
	public void bind() {
		GL15.glBindBuffer(type, pointer);
	}
	
	public void unbind() {
		GL15.glBindBuffer(type, 0);
	}
	
}
