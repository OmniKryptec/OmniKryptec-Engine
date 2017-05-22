package omnikryptec.storing;

import org.lwjgl.util.vector.Vector3f;

public class Entity extends GameObject{

	private TexturedModel model;
	private Vector3f scale = new Vector3f(1, 1, 1);
	
	
	public Entity(TexturedModel model, GameObject parent){
		super(parent);
		this.model = model;
	}
	
	public Vector3f getScale(){
		return scale;
	}
	
	public Entity setScale(Vector3f v){
		this.scale = v;
		return this;
	}
	
	public Entity(TexturedModel model){
		this(model, null);
	}
	
	public Entity(Entity copy){
		setValuesFrom(copy);
		this.model = copy.model;
	}
	
	public TexturedModel getTexturedModel(){
		return model;
	}
	
}
