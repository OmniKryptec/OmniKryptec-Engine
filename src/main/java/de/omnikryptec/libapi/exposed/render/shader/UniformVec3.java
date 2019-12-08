package de.omnikryptec.libapi.exposed.render.shader;

import org.joml.Vector3fc;

public interface UniformVec3 extends Uniform {
    void loadVec3(float x, float y, float z);

    default void loadVec3(final Vector3fc vector) {
        loadVec3(vector.x(), vector.y(), vector.z());
    }

    default void loadVec3(final float[] array) {
        loadVec3(array[0], array[1], array[2]);
    }
}
