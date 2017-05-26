package omnikryptec.entity;

import omnikryptec.model.Model;
import omnikryptec.model.TexturedModel;
import omnikryptec.texture.Texture;
import org.lwjgl.util.vector.Vector3f;

public class Entity extends GameObject{

    private TexturedModel model;
    private Vector3f scale = new Vector3f(1, 1, 1);

    public Entity(TexturedModel model){
        this(model, null);
    }

    public Entity(TexturedModel model, GameObject parent){
        super(parent);
        this.model = model;
    }

    public Entity(Entity copy){
        setValuesFrom(copy);
        this.model = copy.model;
    }

    public final Vector3f getScale(){
        return scale;
    }

    public final Entity setScale(Vector3f v){
        this.scale = v;
        return this;
    }

    public final TexturedModel getTexturedModel(){
        return model;
    }
    
    public static EntityBuilder newEntity() {
        return new EntityBuilder();
    }
    
    public static EntityBuilder newEntity(Model model) {
        return new EntityBuilder(model);
    }
    
    public static EntityBuilder newEntity(Texture texture) {
        return new EntityBuilder(texture);
    }
	
}
