package de.omnikryptec.render3;

import org.joml.Matrix3x2fc;
import org.joml.Vector2fc;
import org.joml.Vector4fc;

public class ArrayFloatCollector implements FloatCollector {
    
    private final float[] floats;
    private int index = 0;
    
    public ArrayFloatCollector(int size) {
        this.floats = new float[size];
    }
    
    @Override
    public FloatCollector put(Matrix3x2fc mat) {
        mat.get(floats, index);
        index += 6;
        return this;
    }
    
    @Override
    public FloatCollector put(Vector2fc vec) {
        put(vec.x()).put(vec.y());
        return this;
    }
    
    @Override
    public FloatCollector put(Vector4fc vec) {
        put(vec.x()).put(vec.y()).put(vec.z()).put(vec.w());
        return this;
    }
    
    @Override
    public FloatCollector put(float f) {
        floats[index] = f;
        index++;
        return this;
    }
    
    @Override
    public FloatCollector put(float[] a, int offset, int length) {
        if (length < 100) {//Pretty arbitrary, idk
            for (int i = 0; i < length; i++) {
                put(a[i + offset]);
            }
        } else {
            System.arraycopy(a, offset, floats, index, length);
            index += length;
        }
        return this;
    }
    
    public float[] getArray() {
        return floats;
    }
    
    @Override
    public int remaining() {
        return floats.length - index;
    }
    
    @Override
    public int position() {
        return index;
    }
    
}
