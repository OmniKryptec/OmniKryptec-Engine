package de.omnikryptec.libapi.exposed.render.shader;

import org.joml.Vector4f;

import de.omnikryptec.util.data.Color;

public interface UniformVec4 extends Uniform {

    void loadVec4(float x, float y, float z, float w);

    default void loadVec4(final Vector4f vector) {
        loadVec4(vector.x, vector.y, vector.z, vector.w);
    }

    default void loadVec4(final float[] array) {
        loadVec4(array[0], array[1], array[2], array[3]);
    }

    default void loadColor(final Color color) {
        loadVec4(color.getR(), color.getG(), color.getB(), color.getA());
    }
}
