package omnikryptec.entity;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import omnikryptec.model.AdvancedModel;
import omnikryptec.model.Model;
import omnikryptec.texture.SimpleTexture;
import omnikryptec.util.Color;

public class Entity extends GameObject implements Rangeable {

	public static enum RenderType {
		ALWAYS, MEDIUM, FOLIAGE, BIG;
	}

	private AdvancedModel model;
	private Vector3f scale = new Vector3f(1, 1, 1);
	private RenderType type = RenderType.ALWAYS;
	private Color color = new Color(1, 1, 1, 1);

	protected Entity() {
            super();
	}
        
	protected Entity(String name) {
            super(name);
	}

	public Entity(AdvancedModel model) {
		this("", model, null);
	}

	public Entity(String name, AdvancedModel model) {
		this(name, model, null);
	}

	public Entity(AdvancedModel model, GameObject parent) {
                this("", model, parent);
	}

	public Entity(String name, AdvancedModel model, GameObject parent) {
		super(name, parent);
		this.model = model;
	}

	public Entity(Entity copy) {
                super(copy.getName());
		setValuesFrom(copy);
		this.model = copy.model;
		this.type = copy.type;
		this.scale = new Vector3f(copy.scale);
		this.color = new Color(copy.color);
	}

	public Entity setRenderType(RenderType type) {
		this.type = type;
		return this;
	}
	
	@Override
	public RenderType getType() {
		return type;
	}

	public final Vector3f getScale() {
		return scale;
	}

	public final Entity setScale(Vector3f v) {
		this.scale = v;
		return this;
	}

	public final Entity setAdvancedModel(AdvancedModel model) {
		this.model = model;
		return this;
	}

	public final AdvancedModel getAdvancedModel() {
		return model;
	}

	public Entity setColor(Color c) {
		this.color = c;
		return this;
	}

	public Entity setColor(float r, float g, float b, float a) {
		color.set(r, g, b, a);
		return this;
	}

	public Color getColor() {
		return color;
	}

	public static EntityBuilder newEntity() {
		return new EntityBuilder();
	}

	public static EntityBuilder newEntity(Model model) {
		return new EntityBuilder(model);
	}

	public static EntityBuilder newEntity(SimpleTexture texture) {
		return new EntityBuilder(texture);
	}

}
