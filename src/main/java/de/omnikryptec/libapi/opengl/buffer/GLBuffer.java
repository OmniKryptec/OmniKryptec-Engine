package de.omnikryptec.libapi.opengl.buffer;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL15;

import de.omnikryptec.libapi.glfw.GLFWManager;

public abstract class GLBuffer {

	private static final List<GLBuffer> all = new ArrayList<>();

	static {
		GLFWManager.registerResourceShutdownHooks(() -> cleanup());
	}

	private static void cleanup() {
		while (!all.isEmpty()) {
			all.get(0).deleteBuffer();
		}
	}

	private final int pointer;
	private final int type;

	public GLBuffer(int type) {
		this.type = type;
		this.pointer = GL15.glGenBuffers();
		all.add(this);
	}

	public void deleteBuffer() {
		GL15.glDeleteBuffers(pointer);
		all.remove(this);
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
