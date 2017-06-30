package omnikryptec.resource.texture;

public class ParticleAtlas {
	private int numberOfRows;
	private boolean usealphablending;
	private Texture tex;

	public ParticleAtlas(Texture t, int numberOfRows, boolean alphablending) {
		this.tex = t;
		this.numberOfRows = numberOfRows;
		this.usealphablending = alphablending;
	}

	public boolean useAlphaBlending() {
		return usealphablending;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public ParticleAtlas setUseAlphaBlending(boolean b) {
		this.usealphablending = b;
		return this;
	}

	public ParticleAtlas setNumberOfRows(int i) {
		this.numberOfRows = i;
		return this;
	}

	public Texture getTexture() {
		return tex;
	}

	public ParticleAtlas setTexture(Texture t) {
		this.tex = t;
		return this;
	}

}
