package omnikryptec.entity;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import omnikryptec.model.Model;
import omnikryptec.model.TexturedModel;
import omnikryptec.texture.SimpleTexture;

public class Entity extends GameObject implements Rangeable{

	public static enum RenderType {
		ALWAYS, MEDIUM, FOLIAGE, BIG;
	}

	private TexturedModel model;
	private Vector3f scale = new Vector3f(1, 1, 1);
	private RenderType type = RenderType.ALWAYS;
	private Vector4f color = new Vector4f(1, 1, 1, 1);

	protected Entity() {
	}

	public Entity(TexturedModel model) {
		this(model, null);
	}

	public Entity(TexturedModel model, GameObject parent) {
		super(parent);
		this.model = model;
	}

	public Entity(Entity copy) {
		setValuesFrom(copy);
		this.model = copy.model;
		this.type = copy.type;
		this.scale = new Vector3f(copy.scale);
		this.color = new Vector4f(copy.color);
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

	public final Entity setTexturedModel(TexturedModel model) {
		this.model = model;
		return this;
	}

	public final TexturedModel getTexturedModel() {
		return model;
	}

	public Entity setColor(Vector4f v) {
		this.color = v;
		return this;
	}

	public Entity setColor(float r, float g, float b, float a) {
		color.x = r;
		color.y = g;
		color.z = b;
		color.w = a;
		return this;
	}

	public Vector4f getColor() {
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
