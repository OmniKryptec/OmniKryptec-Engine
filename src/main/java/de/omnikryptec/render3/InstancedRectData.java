package de.omnikryptec.render3;

import java.nio.FloatBuffer;
import java.util.function.Supplier;

import org.joml.Matrix3x2f;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.util.data.Color;

public class InstancedRectData implements InstanceData {
    
    public static final InstancedRectBatchedRenderer REND = new InstancedRectBatchedRenderer();
    
    @Override
    public BatchedRenderer getBatchedRenderer() {
        return REND;
    }
    
    public Matrix3x2f transform = new Matrix3x2f();
    private float u0 = 0, v0 = 0, u1 = 1, v1 = 1;
    private Color color = new Color(1, 0, 0, 1).randomizeRGB();
    private Texture texture;
    
    public Texture getTexture() {
        return texture;
    }
    
    public void fill(FloatBuffer b) {
        transform.get(b);
        b.position(b.position() + 6);
        b.put(u0).put(v0).put(u1).put(v1);
        color.get(b);
    }
    
}
