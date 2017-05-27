package omnikryptec.entity;

import java.io.File;
import omnikryptec.model.Model;
import omnikryptec.model.TexturedModel;
import omnikryptec.texture.Texture;

/**
 *
 * @author Panzer1119
 */
public class EntityBuilder {
    
    private Model model = null;
    private Texture texture = null;
    private TexturedModel texturedModel = null;
    
    public EntityBuilder() {
    }
    
    public EntityBuilder(Model model) {
        this.model = model;
    }
    
    public EntityBuilder(Texture texture) {
        this.texture = texture;
    }
    
    public EntityBuilder(Model model, Texture texture) {
        this.model = model;
        this.texture = texture;
    }
    
    public final TexturedModel createTexturedModel() {
        if(texturedModel != null) {
            return texturedModel;
        }
        return (texturedModel = new TexturedModel(model, texture));
    }
    
    public final Entity create() {
        return new Entity(createTexturedModel());
    }

    public final Model getModel() {
        return model;
    }

    public final EntityBuilder setModel(Model model) {
        this.model = model;
        return this;
    }

    public final Texture getTexture() {
        return texture;
    }

    public final EntityBuilder setTexture(Texture texture) {
        this.texture = texture;
        return this;
    }
    
    public final EntityBuilder loadModel(String modelPath) {
        this.model = Model.newModel(modelPath);
        return this;
    }
    
    public final EntityBuilder loadModel(File modelFile) {
        this.model = Model.newModel(modelFile);
        return this;
    }
    
    public final EntityBuilder loadTexture(String texturePath) {
        this.texture = Texture.newTexture(texturePath).create();
        return this;
    }
    
    public final EntityBuilder loadTexture(File textureFile) {
        this.texture = Texture.newTexture(textureFile).create();
        return this;
    }
    
}
