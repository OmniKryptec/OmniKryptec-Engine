package omnikryptec.model;

import omnikryptec.texture.Texture;

/**
 * AdvancedModel
 *
 * @author Panzer1119
 */
public interface AdvancedModel {

    /**
     * Returns the model
     *
     * @return Model Model
     */
    public Model getModel();

    /**
     * Returns the texture
     *
     * @return Texture Texture
     */
    public Texture getTexture();

    /**
     * Returns the material
     *
     * @return Material Material
     */
    public Material getMaterial();

    /**
     * Returns the Name
     *
     * @return String Name
     */
    public String getName();

}
