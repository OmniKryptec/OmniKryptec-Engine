package de.omnikryptec.libapi.exposed.render.shader;

import org.joml.Vector2fc;

public interface UniformVec2 {
    void loadVec2(float x, float y);

    default void loadVec2(final Vector2fc vector) {
        loadVec2(vector.x(), vector.y());
    }

    default void loadVec2(final float[] array) {
        loadVec2(array[0], array[1]);
    }
}
