package de.omnikryptec.libapi.opengl.buffer;

import java.util.ArrayList;
import java.util.Collection;

import org.lwjgl.opengl.GL15;

public abstract class GLBuffer {

	private static final Collection<GLBuffer> all = new ArrayList<>();
	
	static void cleanup() {
		for(GLBuffer buffer : all) {
			buffer.deleteBuffer();
		}
	}
	
	private final int pointer;
	private final int type;
	
	public GLBuffer(int type) {
		this.type = type;
		this.pointer = GL15.glGenBuffers();
	}
		
	public void deleteBuffer() {
		GL15.glDeleteBuffers(pointer);
	}
	
	public int bufferId() {
		return pointer;
	}
	
	public int bufferType() {
		return type;
	}
	
	public void bindBuffer() {
		GL15.glBindBuffer(type, pointer);
	}
	
	@Deprecated
	public void unbindBuffer() {
		GL15.glBindBuffer(type, 0);
	}
	
}
