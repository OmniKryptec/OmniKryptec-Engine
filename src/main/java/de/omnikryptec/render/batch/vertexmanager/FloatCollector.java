package de.omnikryptec.render.batch.vertexmanager;

import java.nio.FloatBuffer;

import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.lwjgl.BufferUtils;

//Is this efficient?
public class FloatCollector {

    private final float[] floats;
    private final FloatBuffer fbuffer;

    private int position;

    public FloatCollector(final int size) {
        this.floats = new float[size];
        this.fbuffer = BufferUtils.createFloatBuffer(size);
    }

    public void put(final Vector2fc vec) {
        put(vec.x());
        put(vec.y());
    }

    public void put(final Vector3fc vec) {
        put(vec.x());
        put(vec.y());
        put(vec.z());
    }

    public void put(final Vector4fc vec) {
        put(vec.x());
        put(vec.y());
        put(vec.z());
        put(vec.w());
    }

    public void put(final float f) {
        this.floats[this.position++] = f;
    }

    public void put(final float[] newfloats) {
        put(newfloats, 0, newfloats.length);
    }

    public void put(final float[] newfloats, final int srcPos, final int length) {
        System.arraycopy(newfloats, srcPos, this.floats, this.position, length);
        this.position += length;
    }

    public FloatBuffer flush() {
        this.fbuffer.clear();
        this.fbuffer.put(this.floats, 0, used());
        this.position = 0;
        return this.fbuffer;
    }

    public float[] rawArray() {
        return this.floats;
    }

    public int size() {
        return this.floats.length;
    }

    public int remaining() {
        return this.floats.length - used();
    }

    public int used() {
        return this.position;
    }

}
