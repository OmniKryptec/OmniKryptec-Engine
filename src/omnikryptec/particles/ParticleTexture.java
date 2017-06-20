package omnikryptec.particles;

import omnikryptec.texture.Texture;

public class ParticleTexture{
	private int numberOfRows;
	private boolean durchsichtig;
	private Texture tex;
	
	public ParticleTexture(Texture t, int numberOfRows, boolean durchsichtig) {
		this.tex = t;
		this.numberOfRows = numberOfRows;
		this.durchsichtig = durchsichtig;
	}

	public boolean useAlphaBlending() {
		return durchsichtig;
	}


	public int getNumberOfRows() {
		return numberOfRows;
	}
	
	public Texture getTex(){
		return tex;
	}

}
