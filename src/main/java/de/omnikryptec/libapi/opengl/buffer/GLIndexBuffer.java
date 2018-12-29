package de.omnikryptec.libapi.opengl.buffer;

import java.nio.IntBuffer;

import org.lwjgl.opengl.GL15;

import de.omnikryptec.libapi.exposed.render.IndexBuffer;

public class GLIndexBuffer extends GLBuffer implements IndexBuffer {

    public GLIndexBuffer() {
        super(GL15.GL_ELEMENT_ARRAY_BUFFER);
    }

    @Override
    public void storeData(final IntBuffer data, final boolean dynamic) {
        bindBuffer();
        GL15.glBufferData(bufferType(), data, dynamic ? GL15.GL_DYNAMIC_DRAW : GL15.GL_STATIC_DRAW);
    }

}
