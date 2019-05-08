package de.omnikryptec.util.math.transform;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class Transform2Df extends TransformBase<Vector2fc, Matrix3x2fc, Vector2f, Matrix3x2f, Transform2Df> {
    
    @Override
    protected Matrix3x2f createWM() {
        return new Matrix3x2f();
    }
    
    @Override
    protected Vector2f createWV() {
        return new Vector2f();
    }
    
    @Override
    protected void set(Matrix3x2f set, Matrix3x2fc in) {
        set.set(in);
    }
    
    @Override
    protected void mul(Matrix3x2f leftM, Matrix3x2fc rightM) {
        leftM.mul(rightM);
    }
    
    @Override
    protected void getPosition(Matrix3x2fc from, Vector2f target) {
        //TODO correct? (old is making the vec 0 and then mulling it with the mat)
        target.set(from.m20(), from.m21());
    }
    
    @Override
    protected Transform2Df thiz() {
        return this;
    }
    
}
