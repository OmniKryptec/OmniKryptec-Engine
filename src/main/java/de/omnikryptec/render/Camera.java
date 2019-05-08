package de.omnikryptec.render;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import de.omnikryptec.util.math.transform.Transform3Df;

public class Camera implements IProjection {

    private final Matrix4f projectionMatrix;

    private final Transform3Df transform;

    private final Matrix4f combined;
    private final FrustumIntersection frustumChecker;
    private boolean valid;

    public Camera(final Matrix4f projection) {
        this.projectionMatrix = projection;
        this.transform = new Transform3Df();
        this.combined = new Matrix4f();
        this.frustumChecker = new FrustumIntersection();
        this.valid = false;
        this.transform.addChangeNotifier((n) -> this.valid = false);

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

    private void revalidate() {
        if (!this.valid) {
            //this.transform.worldspace().mul(this.projectionMatrix, this.combined);
            this.projectionMatrix.mul(this.transform.worldspace(), this.combined);
            this.frustumChecker.set(this.combined);
            this.valid = true;
        }
    }
}
