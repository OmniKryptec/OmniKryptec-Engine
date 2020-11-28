package de.omnikryptec.render3;

import java.nio.FloatBuffer;

import org.joml.Matrix3x2f;

public class InstancedRectData implements InstanceData {
    
    public static final InstancedRectBatchedRenderer REND = new InstancedRectBatchedRenderer();
    
    @Override
    public BatchedRenderer getBatchedRenderer() {
        
        return REND;
    }
    
    public Matrix3x2f transform = new Matrix3x2f();
    
    public void fill(FloatBuffer b) {
        transform.get(b);
        b.position(b.position() + 6);
    }
    
}
