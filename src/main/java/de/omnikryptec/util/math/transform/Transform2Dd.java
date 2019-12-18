package de.omnikryptec.util.math.transform;

import org.joml.Matrix3x2d;
import org.joml.Matrix3x2dc;
import org.joml.Vector2d;
import org.joml.Vector2dc;

public class Transform2Dd extends TransformBase<Vector2dc, Matrix3x2dc, Vector2d, Matrix3x2d, Transform2Dd> {
    
    @Override
    protected Matrix3x2d createWM() {
        return new Matrix3x2d();
    }
    
    @Override
    protected Vector2d createWV() {
        return new Vector2d();
    }
    
    @Override
    protected void set(final Matrix3x2d set, final Matrix3x2dc in) {
        set.set(in);
    }
    
    @Override
    protected void mul(final Matrix3x2d leftM, final Matrix3x2dc rightM) {
        leftM.mul(rightM);
    }
    
    @Override
    protected void getPosition(final Matrix3x2dc from, final Vector2d target) {
        target.set(from.m20(), from.m21());
    }
    
    @Override
    protected Transform2Dd thiz() {
        return this;
    }
    
}
