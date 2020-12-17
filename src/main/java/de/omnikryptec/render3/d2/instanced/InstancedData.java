package de.omnikryptec.render3.d2.instanced;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.TextureRegion;
import de.omnikryptec.render3.d2.IBatchedRenderer2D;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.data.fc.FloatCollector;

public class InstancedData extends AbstractInstancedData {
    
    private Matrix3x2f transform = new Matrix3x2f();
    private float u0 = 0, v0 = 0, u1 = 1, v1 = 1;
    private Color color = new Color();
    private Vector2f sdData = new Vector2f(0, 1);
    private Vector2f bsdData = new Vector2f(0, 0.00000000001f);//TODO having this font stuff in the default renderer is meh i guess
    private Color borderColor = new Color();
    private Vector2f offset = new Vector2f(0);
    private float tiling = 1;
    
    public void fill(FloatCollector b) {
        b.put(transform);
        b.put(u0).put(v0).put(u1).put(v1);
        color.get(b);
        borderColor.get(b);
        b.put(sdData).put(bsdData);
        b.put(offset);
        b.put(tiling);
    }
    
    public void setUVAndTexture(Texture t) {
        setUVAndTexture(t, false, false);
    }
    
    public void setUVAndTexture(final Texture t, final boolean flipU, boolean flipV) {
        this.setTexture(t);
        if (t == null) {
            return;
        }
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
    
    public Matrix3x2f getTransform() {
        return transform;
    }
    //Hmmmmmm.
    public void setTransform(Matrix3x2fc mat) {
        this.transform.set(mat);
    }
    
    public void setTilingFactor(float f) {
        this.tiling = f;
    }
    
    @Override
    public Class<? extends IBatchedRenderer2D> getDefaultRenderer() {
        return InstancedBatch2D.class;
    }
}
