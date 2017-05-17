package omnikryptec.storing;

public class Entity extends GameObject{

	private TexturedModel model;
	
	public Entity(TexturedModel model, GameObject parent){
		super(parent);
		this.model = model;
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
