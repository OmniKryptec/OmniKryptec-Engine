package de.omnikryptec.render.batch;

import java.nio.FloatBuffer;

import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.lwjgl.BufferUtils;

public class FloatCollector {

    private final float[] floats;
    private final FloatBuffer fbuffer;

    private int position;

    public FloatCollector(int size) {
        floats = new float[size];
        fbuffer = BufferUtils.createFloatBuffer(size);
    }

    public void put(Vector2fc vec) {
        put(vec.x());
        put(vec.y());
    }

    public void put(Vector3fc vec) {
        put(vec.x());
        put(vec.y());
        put(vec.z());
    }

    public void put(Vector4fc vec) {
        put(vec.x());
        put(vec.y());
        put(vec.z());
        put(vec.w());
    }

    public void put(float f) {
        floats[position++] = f;
    }

    public void put(float[] newfloats) {
        put(newfloats, 0, newfloats.length);
    }

    public void put(float[] newfloats, int srcPos, int length) {
        System.arraycopy(newfloats, srcPos, floats, position, length);
        position += length;
    }

    public FloatBuffer flush() {
        fbuffer.clear();
        fbuffer.put(floats, 0, position + 1);
        position = 0;
        return fbuffer;
    }

    public int size() {
        return floats.length;
    }

    public int remaining() {
        return floats.length - position;
    }

    public int used() {
        return position + 1;
    }

}
