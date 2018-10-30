package de.omnikryptec.libapi.opengl.buffer;

import org.lwjgl.opengl.GL15;

public class GLVertexBuffer extends GLBuffer {

    public GLVertexBuffer() {
	super(GL15.GL_ARRAY_BUFFER);
    }

}
