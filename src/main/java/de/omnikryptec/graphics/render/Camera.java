package de.omnikryptec.graphics.render;

import org.joml.Matrix4f;

public class Camera implements IProjection {

    private final Matrix4f projectionMatrix;

    public Camera(final Matrix4f projection) {
        this.projectionMatrix = projection;
    }

    @Override
    public Matrix4f getProjection() {
        return this.projectionMatrix;
    }

}
