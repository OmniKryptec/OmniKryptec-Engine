package omnikryptec.terrain;

import omnikryptec.resource.texture.Texture;

/**
 *
 * @author Panzer1119
 */
public class TerrainTexturePack {

	private final Texture backgroundTexture;
	private final Texture rTexture;
	private final Texture gTexture;
	private final Texture bTexture;

	public TerrainTexturePack(Texture backgroundTexture, Texture rTexture, Texture gTexture, Texture bTexture) {
		this.backgroundTexture = backgroundTexture;
		this.rTexture = rTexture;
		this.gTexture = gTexture;
		this.bTexture = bTexture;
	}

	public Texture getBackgroundTexture() {
		return backgroundTexture;
	}

	public Texture getrTexture() {
		return rTexture;
	}

	public Texture getgTexture() {
		return gTexture;
	}

	public Texture getbTexture() {
		return bTexture;
	}

}
