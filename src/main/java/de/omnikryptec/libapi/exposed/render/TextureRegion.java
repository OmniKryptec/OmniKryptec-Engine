package de.omnikryptec.libapi.exposed.render;

public class TextureRegion implements Texture {
    
    private final Texture superTexture;
    private final float[] uvs = { 0, 0, 1, 1 };
    private final float width, height;
    
    public TextureRegion(Texture superTexture, float u0, float v0, float u1, float v1) {
        this.superTexture = superTexture;
        float width = superTexture.getWidth();
        float height = superTexture.getHeight();
        if (superTexture instanceof TextureRegion) {
            TextureRegion tmp = (TextureRegion) superTexture;
            width *= (Math.abs(u1 - u0));
            height *= (Math.abs(v1 - v0));
            float tmpf1 = -tmp.u0() + tmp.v0();
            float tmpf2 = -tmp.u1() + tmp.v1();
            u0 *= tmpf1;
            v0 *= tmpf2;
            u1 *= tmpf1;
            v1 *= tmpf2;
            u0 += tmp.u0();
            v0 += tmp.v0();
            u1 += tmp.u1();
            v1 += tmp.v1();
        }
        uvs[0] = u0;
        uvs[1] = v0;
        uvs[2] = u1;
        uvs[3] = v1;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void bindTexture(int unit) {
        superTexture.bindTexture(unit);
    }
    
    @Override
    public float getWidth() {
        return width;
    }
    
    @Override
    public float getHeight() {
        return height;
    }
    
    public float u0() {
        return uvs[0];
    }
    
    public float v0() {
        return uvs[1];
    }
    
    public float u1() {
        return uvs[2];
    }
    
    public float v1() {
        return uvs[3];
    }
    
    public Texture getSuperTexture() {
        return superTexture;
    }
    
    public Texture getBaseTexture() {
        if (superTexture instanceof TextureRegion) {
            return ((TextureRegion) superTexture).getBaseTexture();
        }
        return superTexture;
    }
    
}
