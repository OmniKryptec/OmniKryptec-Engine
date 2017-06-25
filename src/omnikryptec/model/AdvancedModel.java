package omnikryptec.model;

import omnikryptec.loader.ResourceObject;
import omnikryptec.test.saving.DataMap;
import omnikryptec.texture.Texture;

/**
 * AdvancedModel
 *
 * @author Panzer1119
 */
public interface AdvancedModel extends ResourceObject {

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
    
    /**
     * Deletes the Model
     * 
     * @return A reference to this AdvancedModel
     */
    public AdvancedModel delete();
    
    /**
     * Copies this AdvancedModel
     * 
     * @return A copy of this AdvancedModel
     */
    public AdvancedModel copy();
    
    /**
     * Returns a representation of this object in a DataMap
     * 
     * @param data DataMap Data
     * @return DataMap Data
     */
    public DataMap toDataMap(DataMap data);
    
    /**
     * Loads values from a DataMap
     * 
     * @param data DataMap Data
     * @return A reference to this AdvancedModel
     */
    public AdvancedModel fromDataMap(DataMap data);

}
