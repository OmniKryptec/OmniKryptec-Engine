package de.omnikryptec.libapi.exposed.render;

//TODO pcfreak9000 fix TextureRegion of a TextureRegion, fix requiresInvertedVIFdrawn
public class TextureRegion implements Texture {
    
    private final Texture superTexture;
    private final float[] uvs = { 0, 0, 1, 1 };
    private final float width, height;
    
    public TextureRegion(final Texture superTexture, float u0, float v0, float u1, float v1) {
        this.superTexture = superTexture;
        float width = superTexture.getWidth();
        float height = superTexture.getHeight();
        if (superTexture instanceof TextureRegion) {
            final TextureRegion tmp = (TextureRegion) superTexture;
            final float tmpf1 = -tmp.u0() + tmp.v0();
            final float tmpf2 = -tmp.u1() + tmp.v1();
            u0 *= tmpf1;
            v0 *= tmpf2;
            u1 *= tmpf1;
            v1 *= tmpf2;
            u0 += tmp.u0();
            v0 += tmp.v0();
            u1 += tmp.u1();
            v1 += tmp.v1();
        }
        this.uvs[0] = u0;
        this.uvs[1] = v0;
        this.uvs[2] = u1;
        this.uvs[3] = v1;
        width *= (Math.abs(u1 - u0));
        height *= (Math.abs(v1 - v0));
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void bindTexture(final int unit) {
        this.superTexture.bindTexture(unit);
    }
    
    @Override
    public float getWidth() {
        return this.width;
    }
    
    @Override
    public float getHeight() {
        return this.height;
    }
    
    public float u0() {
        return this.uvs[0];
    }
    
    public float v0() {
        return this.uvs[1];
    }
    
    public float u1() {
        return this.uvs[2];
    }
    
    public float v1() {
        return this.uvs[3];
    }
    
    public Texture getSuperTexture() {
        return this.superTexture;
    }
    
    @Override
    public Texture getBaseTexture() {
        if (this.superTexture instanceof TextureRegion) {
            return ((TextureRegion) this.superTexture).getBaseTexture();
        }
        return this.superTexture;
    }
    
    @Override
    public boolean requiresInvertedVifDrawn2D() {
        return this.superTexture.requiresInvertedVifDrawn2D();
    }
    
}
