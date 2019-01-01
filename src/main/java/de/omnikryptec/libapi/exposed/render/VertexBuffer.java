package de.omnikryptec.libapi.exposed.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public interface VertexBuffer {

    void bindBuffer();

    void unbindBuffer();

    void storeData(FloatBuffer data, boolean dynamic, int size);

    default void storeData(final float[] data, final boolean dynamic) {
        final FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        storeData(buffer, dynamic, data.length);
    }

    void storeData(IntBuffer data, boolean dynamic, int size);

    default void storeData(final int[] data, final boolean dynamic) {
        final IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        storeData(buffer, dynamic, data.length);
    }
    
    int size();
}
