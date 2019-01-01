package de.omnikryptec.libapi.exposed.render;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public interface IndexBuffer {
    
    void bindBuffer();
    
    void unbindBuffer();
    
    void storeData(IntBuffer data, boolean dynamic, int size);
    
    default void storeData(final int[] data, final boolean dynamic) {
        final IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        storeData(buffer, dynamic, data.length);
    }
    
    int size();
    
}
