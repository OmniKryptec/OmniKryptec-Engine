package omnikryptec.resource.texture;

import org.lwjgl.opengl.GL11;

import omnikryptec.resource.loader.ResourceObject;

public abstract class Texture implements ResourceObject {

    private static Texture lastBoundTexture;

    private final String name;
    private boolean alwaysBind = false;
    private float[] uvs = {0, 0, 1, 1};

    public Texture(String name) {
        this(name, false);
    }

    public Texture(String name, float u, float v, float u2, float v2) {
        this(name, false, u, v, u2, v2);
    }

    public Texture(String name, boolean alwaysBind) {
        this(name, alwaysBind, 0, 0, 1, 1);
    }

    public Texture(String name, boolean alwaysBind, float u, float v, float u2, float v2) {
        this.name = name;
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
            lastBoundTexture = this;
        }
    }

    protected abstract void bindToUnita(int unit, int... info);

    public float[] getUVs() {
        return uvs;
    }

    public boolean bindAlways() {
        return alwaysBind;
    }

    @Override
    public final String getName() {
        return name;
    }

    public static void resetLastBoundTexture(){
    	lastBoundTexture = null;
    }
    
	public static void unbindCurrent() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		resetLastBoundTexture();
	}
	
	public static void bind(int type, int id){
		resetLastBoundTexture();
		GL11.glBindTexture(type, id);
	}

}
