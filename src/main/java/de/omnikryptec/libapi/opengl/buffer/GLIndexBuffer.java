package de.omnikryptec.libapi.opengl.buffer;

import java.nio.IntBuffer;

import org.lwjgl.opengl.GL15;

import de.omnikryptec.libapi.exposed.render.IndexBuffer;

public class GLIndexBuffer extends GLBuffer implements IndexBuffer {
    
    private int size;
    
    public GLIndexBuffer() {
        super(GL15.GL_ELEMENT_ARRAY_BUFFER);
    }
    
    @Override
    public void storeData(final IntBuffer data, final boolean dynamic, int size) {
        this.size = size;
        data.flip();
        bindBuffer();
        GL15.glBufferData(bufferType(), data, dynamic ? GL15.GL_DYNAMIC_DRAW : GL15.GL_STATIC_DRAW);
    }
    
    @Override
    public int size() {
        return size;
    }
    
}
