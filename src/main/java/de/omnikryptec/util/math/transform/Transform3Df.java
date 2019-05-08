package de.omnikryptec.util.math.transform;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Transform3Df extends TransformBase<Vector3fc, Matrix4fc, Vector3f, Matrix4f, Transform3Df> {
    
    @Override
    protected Matrix4f createWM() {
        return new Matrix4f();
    }
    
    @Override
    protected Vector3f createWV() {
        return new Vector3f();
    }
    
    @Override
    protected void set(Matrix4f set, Matrix4fc in) {
        set.set(in);
    }
    
    @Override
    protected void mul(Matrix4f leftM, Matrix4fc rightM) {
        leftM.mul(rightM);
    }
    
    @Override
    protected void getPosition(Matrix4fc from, Vector3f target) {
        //TODO correct? see Test2D
        target.set(from.m30(), from.m31(), from.m32());
    }

    @Override
    protected Transform3Df thiz() {
        return this;
    }
    
}
