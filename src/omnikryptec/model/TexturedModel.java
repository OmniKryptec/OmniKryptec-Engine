package omnikryptec.model;

import omnikryptec.texture.Texture;

public class TexturedModel implements AdvancedModel {

    private String name;
    private Model model;
    private Texture texture;
    private Material material;

    public TexturedModel(String name, Model model, Texture texture) {
        this(name, model, texture, new Material());
    }

    public TexturedModel(String name, Model model, Texture texture, Material material) {
        this.name = name;
        this.model = model;
        this.texture = texture;
        this.material = material;
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

    @Override
    public String getName() {
        return name;
    }

}
