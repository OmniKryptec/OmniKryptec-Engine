package de.omnikryptec.graphics.render;

import org.joml.Matrix4f;

public class Camera {
    
    private Matrix4f projectionMatrix;
    
    public Camera(Matrix4f projection) {
        this.projectionMatrix = projection;
    }
    
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
    
}
