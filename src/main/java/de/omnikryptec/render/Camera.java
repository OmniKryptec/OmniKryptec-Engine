package de.omnikryptec.render;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import de.omnikryptec.util.math.Transform;

public class Camera implements IProjection {
    
    private final Matrix4f projectionMatrix;
    
    private Transform transform;
    
    private Matrix4f combined;
    private FrustumIntersection frustumChecker;
    private boolean valid;
    
    public Camera(final Matrix4f projection) {
        this.projectionMatrix = projection;
        this.transform = new Transform();
        this.combined = new Matrix4f();
        this.frustumChecker = new FrustumIntersection();
        this.valid = true;
        this.transform.addChangeNotifier((n) -> valid = false);
        
    }
    
    @Override
    public Matrix4fc getRawProjection() {
        return projectionMatrix;
    }
    
    @Override
    public Matrix4fc getProjection() {
        revalidate();
        return combined;
    }
    
    @Override
    public FrustumIntersection getFrustumTester() {
        revalidate();
        return frustumChecker;
    }
    
    public Transform getTransform() {
        return transform;
    }
    
    private void revalidate() {
        if (!valid) {
            transform.worldspace().mul(this.projectionMatrix, combined);
            frustumChecker.set(combined);
            valid = true;
        }
    }
}
