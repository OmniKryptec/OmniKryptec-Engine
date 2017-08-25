package omnikryptec.resource.texture;

import org.lwjgl.opengl.GL11;

import omnikryptec.graphics.OpenGL;
import omnikryptec.resource.loader.ResourceObject;

public abstract class Texture implements ResourceObject {

	public static final int MAX_SUPPORTED_TEXTURE_UNITS=10;
	
    private static Texture[] lastBoundTexture = new Texture[MAX_SUPPORTED_TEXTURE_UNITS];

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

    public final void bindToUnitOptimized(int unit, int... info) {
    	if (this != lastBoundTexture[unit] || alwaysBind) {
            bindToUnit(unit, info);
            lastBoundTexture[unit] = this;
        }
    }

    protected abstract void bindToUnit(int unit, int... info);

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
    	for(int i=0; i<lastBoundTexture.length; i++) {
    		lastBoundTexture[i] = null;
    	}
    }
    
	public static void unbindActive() {
		bindTexture(GL11.GL_TEXTURE_2D, 0);
		resetLastBoundTexture();
	}
	
	public static void bindAndReset(int type, int id){
		bindTexture(type, id);
		resetLastBoundTexture();
	}
	
	/**
	 * should only be used in {@link #bindToUnit(int, int...)}
	 * @param type
	 * @param id
	 */
	protected static void bindTexture(int type, int id){
		OpenGL.gl11bindTexture(type, id);
	}

}
