package de.omnikryptec.render3.d2.instanced;

import org.joml.Matrix3x2f;
import org.joml.Vector2f;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.TextureRegion;
import de.omnikryptec.render3.d2.IBatchedRenderer2D;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.data.fc.FloatCollector;

public class InstancedData extends AbstractInstancedData {
    
    private Matrix3x2f transform = new Matrix3x2f();
    private float u0 = 0, v0 = 0, u1 = 1, v1 = 1;
    private Color color = new Color(1, 0, 0, 1).randomizeRGB();
    private Vector2f sdData = new Vector2f();
    private Vector2f bsdData = new Vector2f();
    private Color borderColor = new Color();
    private Vector2f offset = new Vector2f();
    
    public void fill(FloatCollector b) {
        b.put(transform);
        b.put(u0).put(v0).put(u1).put(v1);
        color.get(b);
        borderColor.get(b);
        b.put(sdData).put(bsdData);
        b.put(offset);
    }
    
    public void setUVAndTexture(Texture t) {
        setUVAndTexture(t, false, false);
    }
    
    public void setUVAndTexture(final Texture t, final boolean flipU, boolean flipV) {
        if (t == null) {
            return;
        }
        this.setTexture(t);
        flipV = flipV != t.requiresInvertedVifDrawn2D();
        if (t instanceof TextureRegion) {
            final TextureRegion r = (TextureRegion) t;
            this.u0 = r.u0();
            this.v0 = r.v0();
            this.u1 = r.u1();
            this.v1 = r.v1();
        } else {
            this.u0 = 0;
            this.v0 = 0;
            this.u1 = 1;
            this.v1 = 1;
        }
        if (flipU) {
            float tmp = this.u0;
            this.u0 = this.u1;
            this.u1 = tmp;
        }
        if (flipV) {
            float tmp = this.v0;
            this.v0 = this.v1;
            this.v1 = tmp;
        }
    }
    
    public Color color() {
        return color;
    }
    
    public void setUV(float u0, float v0, float u1, float v1) {
        this.u0 = u0;
        this.v0 = v0;
        this.u1 = u1;
        this.v1 = v1;
    }
    
    public Vector2f sdData() {
        return sdData;
    }
    
    public Vector2f bsdData() {
        return bsdData;
    }
    
    public Color borderColor() {
        return borderColor;
    }
    
    public Vector2f offset() {
        return offset;
    }
    
    public Matrix3x2f transform() {
        return transform;
    }
    
    @Override
    public Class<? extends IBatchedRenderer2D> getDefaultRenderer() {
        return InstancedBatch2D.class;
    }
}
