package de.omnikryptec.render3;

import java.nio.FloatBuffer;

import org.joml.Matrix3x2fc;
import org.lwjgl.BufferUtils;

public class BufferFloatCollector implements FloatCollector {
    
    private final FloatBuffer buffer;
    
    public BufferFloatCollector(int size) {
        this.buffer = BufferUtils.createFloatBuffer(size);
    }
    
    @Override
    public FloatCollector put(Matrix3x2fc mat) {
        mat.get(buffer);
        buffer.position(buffer.position() + 6);
        return this;
    }
    
    @Override
    public FloatCollector put(float f) {
        buffer.put(f);
        return this;
    }
    
    @Override
    public FloatCollector put(float[] a, int offset, int length) {
        buffer.put(a, offset, length);
        return this;
    }
    
    public FloatBuffer getBuffer() {
        return buffer;
    }
    
    @Override
    public int remaining() {
        return buffer.remaining();
    }
    
    @Override
    public int position() {
        return buffer.position();
    }
}
