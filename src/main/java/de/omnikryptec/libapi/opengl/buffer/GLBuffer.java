package de.omnikryptec.libapi.opengl.buffer;

import java.util.ArrayList;
import java.util.Collection;

import org.lwjgl.opengl.GL15;

public abstract class GLBuffer {

	private static final Collection<GLBuffer> all = new ArrayList<>();
	
	static void cleanup() {
		for(GLBuffer buffer : all) {
			buffer.delete();
		}
	}
	
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
