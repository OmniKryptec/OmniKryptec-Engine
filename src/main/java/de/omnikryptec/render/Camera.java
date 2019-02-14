package de.omnikryptec.render;

import org.joml.FrustumIntersection;
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
    
    @Override
    public FrustumIntersection getFrustumTester() {

        return null;
    }
    
}