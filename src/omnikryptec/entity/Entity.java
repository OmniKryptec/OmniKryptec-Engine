package omnikryptec.entity;

import org.lwjgl.util.vector.Vector3f;

import omnikryptec.model.Model;
import omnikryptec.model.TexturedModel;
import omnikryptec.texture.Texture;

public class Entity extends GameObject{

	public static enum RenderType{
		NORMAL,FOLIAGE;
	}
	
    private TexturedModel model;
    private Vector3f scale = new Vector3f(1, 1, 1);
    private RenderType type = RenderType.NORMAL;
    
    
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

    public Entity setRenderType(RenderType type){
    	this.type = type;
    	return this;
    }
    
    public RenderType getType(){
    	return type;
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
