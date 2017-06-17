package omnikryptec.model;

import omnikryptec.texture.Texture;

public class TexturedModel {

	private Model model;
	private Texture texture;
	private Material material;

	public TexturedModel(Model m, Texture t) {
		this(m, t, new Material());
	}

	public TexturedModel(Model m, Texture t, Material mat) {
		this.model = m;
		this.texture = t;
		this.material = mat;
	}

	public Model getModel() {
		return model;
	}

	public Texture getTexture() {
		return texture;
	}

	public Material getMaterial() {
		return material;
	}

}
