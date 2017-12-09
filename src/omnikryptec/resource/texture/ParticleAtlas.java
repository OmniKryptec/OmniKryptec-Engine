package omnikryptec.resource.texture;

import omnikryptec.graphics.GraphicsUtil.BlendMode;

public class ParticleAtlas {
	private int numberOfRows;
	private BlendMode blenddmode;
	private Texture tex;

	public ParticleAtlas(Texture t, int numberOfRows, BlendMode blenddmode) {
		this.tex = t;
		this.numberOfRows = numberOfRows;
		this.blenddmode = blenddmode;
	}

	public BlendMode getBlendMode() {
		return blenddmode;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public ParticleAtlas setBlendMode(BlendMode b) {
		this.blenddmode = b;
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
