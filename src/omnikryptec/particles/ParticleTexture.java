package omnikryptec.particles;

import omnikryptec.resource.texture.Texture;

public class ParticleTexture {
	private int numberOfRows;
	private boolean usealphablending;
	private Texture tex;

	public ParticleTexture(Texture t, int numberOfRows, boolean durchsichtig) {
		this.tex = t;
		this.numberOfRows = numberOfRows;
		this.usealphablending = durchsichtig;
	}

	public boolean useAlphaBlending() {
		return usealphablending;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public Texture getTexture() {
		return tex;
	}

}
