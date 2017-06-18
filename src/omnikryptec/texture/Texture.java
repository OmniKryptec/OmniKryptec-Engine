package omnikryptec.texture;

public interface Texture {
	
	/**
	 * u,v,u2,v2
	 */
	public static final float[] DEFAULT_UVS = {0,0,1,1};
	
	void bindToUnit(int unit, int... info);
	
	default float[] getUVs(){
		return DEFAULT_UVS;
	}
	
	
}
