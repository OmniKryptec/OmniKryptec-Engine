package omnikryptec.texture;

import omnikryptec.loader.ResourceObject;

public abstract class Texture implements ResourceObject {

    private static Texture lastBoundTexture;

    private boolean alwaysBind = false;
    private float[] uvs = {0, 0, 1, 1};

    public Texture() {
        this(false);
    }

    public Texture(float u, float v, float u2, float v2) {
        this(false, u, v, u2, v2);
    }

    public Texture(boolean alwaysBind) {
        this(alwaysBind, 0, 0, 1, 1);
    }

    public Texture(boolean alwaysBind, float u, float v, float u2, float v2) {
        this.alwaysBind = alwaysBind;
        uvs[0] = u;
        uvs[1] = v;
        uvs[2] = u2;
        uvs[3] = v2;
    }

    protected Texture setUVs(float u, float v, float u2, float v2) {
        uvs[0] = u;
        uvs[1] = v;
        uvs[2] = u2;
        uvs[3] = v2;
        return this;
    }

    public final void bindToUnit(int unit, int... info) {
        if (this != lastBoundTexture || alwaysBind) {
            bindToUnita(unit, info);
        }
    }

    protected abstract void bindToUnita(int unit, int... info);

    public float[] getUVs() {
        return uvs;
    }

    public boolean bindAlways() {
        return alwaysBind;
    }

}
