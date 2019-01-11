package de.omnikryptec.graphics.render;

import org.joml.Matrix4f;

public class Camera implements IProjection{
    
    private Matrix4f projectionMatrix;
    
    public Camera(Matrix4f projection) {
        this.projectionMatrix = projection;
    }
    
    @Override
    public Matrix4f getProjection() {
        return projectionMatrix;
    }
    
}
