package de.omnikryptec.libapi.exposed.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public interface VertexBuffer {
    
    void bindBuffer();
    
    void unbindBuffer();
    
    void storeData(FloatBuffer data, boolean dynamic);
    
    default void storeData(float[] data, boolean dynamic) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        storeData(buffer, dynamic);
    }
    
    void storeData(IntBuffer data, boolean dynamic);
    
    default void storeData(int[] data, boolean dynamic) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        storeData(buffer, dynamic);
    }
}