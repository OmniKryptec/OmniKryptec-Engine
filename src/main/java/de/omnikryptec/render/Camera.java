package de.omnikryptec.render;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import de.omnikryptec.util.math.transform.Transform3Df;

public class Camera implements IProjection {
    
    private Matrix4f inverseCombined;
    
    private Matrix4f projectionMatrix;
    
    private Transform3Df transform;
    
    private final Matrix4f combined;
    private final FrustumIntersection frustumChecker;
    private boolean valid;
    
    public Camera(final Matrix4f projection) {
        this.projectionMatrix = projection;
        this.combined = new Matrix4f();
        this.inverseCombined = new Matrix4f();
        this.frustumChecker = new FrustumIntersection();
        this.valid = false;
        setTransform(new Transform3Df());
    }
    
    public void setProjection(final Matrix4f projection) {
        this.projectionMatrix = projection;
        this.valid = false;
    }
    
    @Override
    public Matrix4fc getRawProjection() {
        return this.projectionMatrix;
    }
    
    @Override
    public Matrix4fc getProjection() {
        revalidate();
        return this.combined;
    }
    
    @Override
    public FrustumIntersection getFrustumTester() {
        revalidate();
        return this.frustumChecker;
    }
    
    public Transform3Df getTransform() {
        return this.transform;
    }
    
    public void setTransform(final Transform3Df trans) {
        this.transform = trans;
        this.transform.setChangeNotifier((n) -> this.valid = false);
    }
    
    private void revalidate() {
        if (!this.valid) {
            //this.transform.worldspace().mul(this.projectionMatrix, this.combined);
            this.projectionMatrix.mul(this.transform.worldspace(), this.combined);
            this.frustumChecker.set(this.combined);
            this.inverseCombined = this.combined.invert(this.inverseCombined);
            this.valid = true;
        }
    }
    
    public Matrix4fc getProjectionInverse() {
        return this.inverseCombined;
    }
}
