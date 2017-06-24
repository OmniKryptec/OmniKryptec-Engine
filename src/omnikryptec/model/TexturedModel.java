package omnikryptec.model;

import omnikryptec.texture.Texture;

public class TexturedModel implements AdvancedModel {

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

	@Override
	public final Model getModel() {
		return model;
	}

	@Override
	public final Texture getTexture() {
		return texture;
	}

	@Override
	public final Material getMaterial() {
		return material;
	}

	public final TexturedModel setMaterial(Material m) {
		this.material = m;
		return this;
	}

}
