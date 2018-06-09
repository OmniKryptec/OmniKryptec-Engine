package omnikryptec.gameobject.particlesV2;

import omnikryptec.resource.texture.ParticleAtlas;

public class ParticleDefinition {

	public static final int RENDER_FLOAT_SIZE = 25;
	
	private int logic_float_size;;
	private ParticleAtlas texture;
	
	public ParticleDefinition(int logicdatasize) {
		this.logic_float_size = logicdatasize;
	}

	public int getLogicFloatSize() {
		return logic_float_size;
	}
	
}
