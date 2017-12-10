package omnikryptec.resource.texture;

import omnikryptec.resource.loader.ResourceObject;

public class AtlasTexture extends Texture {

    private Texture texture;
    private float w,h;
    
    public AtlasTexture(Texture t, float u, float v, float u2, float v2) {
        this("", t, u, v, u2, v2);
    }

    public AtlasTexture(String name, Texture t, float u, float v, float u2, float v2) {
        super(name, t.bindAlways());
        this.texture = t;
        setTexCoords(u, v, u2, v2);
    }

    public AtlasTexture setTexCoords(float u, float v, float u2, float v2) {
        if (texture instanceof AtlasTexture) {
            AtlasTexture tmp = (AtlasTexture) texture;
            w = texture.getWidth()*(Math.abs(u2-u));
            h = texture.getHeight()*(Math.abs(v2-v));
            float tmpf1 = -tmp.getUVs()[0] + tmp.getUVs()[2];
            float tmpf2 = -tmp.getUVs()[1] + tmp.getUVs()[3];
            u *= tmpf1;
            v *= tmpf2;
            u2 *= tmpf1;
            v2 *= tmpf2;
            u += tmp.getUVs()[0];
            u2 += tmp.getUVs()[1];
            v += tmp.getUVs()[2];
            v2 += tmp.getUVs()[3];
        }
        setUVs(u, v, u2, v2);
        return this;
    }

    /**
     * can break the UV-Coords!
     *
     * @param t
     * @return
     */
    public AtlasTexture setTexture(Texture t) {
        this.texture = t;
        return this;
    }

    public Texture getTexture() {
        return this.texture;
    }

    @Override
    public boolean bindAlways() {
        return texture.bindAlways();
    }

    @Override
    public void bindToUnit(int unit, int... info) {
        texture.bindToUnit(unit, info);
    }

	@Override
	public float getWidth() {
		return w;
	}

	@Override
	public float getHeight() {
		return h;
	}

	@Override
	public ResourceObject delete() {
		return this;
	}

}
