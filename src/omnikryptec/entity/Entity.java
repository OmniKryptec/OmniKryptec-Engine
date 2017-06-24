package omnikryptec.entity;

import omnikryptec.logger.Logger;
import org.lwjgl.util.vector.Vector3f;

import omnikryptec.model.AdvancedModel;
import omnikryptec.model.Model;
import omnikryptec.test.saving.DataMap;
import omnikryptec.test.saving.DataMapSerializable;
import omnikryptec.texture.SimpleTexture;
import omnikryptec.util.Color;
import omnikryptec.util.SerializationUtil;

public class Entity extends GameObject implements DataMapSerializable, Rangeable {

    public static enum RenderType {
        ALWAYS, MEDIUM, FOLIAGE, BIG;
    }

    private AdvancedModel model;
    private Vector3f scale = new Vector3f(1, 1, 1);
    private RenderType type = RenderType.ALWAYS;
    private Color color = new Color(1, 1, 1, 1);

    public Entity() {
        super();
    }

    public Entity(String name) {
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

    public Entity setValuesFrom(Entity toCopy) {
        if (toCopy == null) {
            return this;
        }
        setName(toCopy.getName());
        setActive(toCopy.isActive());
        setParent(toCopy.getParent());
        setRotation(new Vector3f(toCopy.getRotation()));
        setPos(new Vector3f(toCopy.getRelativePos()));
        color = toCopy.color.getClone();
        model = toCopy.model;
        scale = new Vector3f(toCopy.scale);
        type = toCopy.type;
        return this;
    }

    @Override
    public DataMap toDataMap(DataMap data) {
        DataMap data_temp = super.toDataMap(new DataMap("gameObject"));
        data.put(data_temp.getName(), data_temp);
        data.put("color", SerializationUtil.colorToString(color));
        data.put("scale", SerializationUtil.vector3fToString(scale));
        data.put("type", type.name());
        data.put("modelName", model.getName());
        data.put("modelClass", model.getClass().getName());
        return data;
    }

    public static Entity newInstanceFromDataMap(DataMap data) {
        if (data == null) {
            return null;
        }
        String name = data.getDataMap("gameObject").getString("name");
        if(name == null || name.isEmpty()) {
            Logger.log("Failed to create new Entity");
            return null;
        }
        final Entity entity = byName(Entity.class, name, false);
        return (entity != null ? entity : new Entity()).fromDataMap(data);
    }

    @Override
    public Entity fromDataMap(DataMap data) {
        if (data == null) {
            return null;
        }
        Logger.log("Creating Entity from DataMap");
        DataMap dataMap_temp = data.getDataMap("gameObject");
        super.fromDataMap(dataMap_temp);
        color = SerializationUtil.stringToColor(data.getString("color"));
        scale = SerializationUtil.stringToVector3f(data.getString("scale"));
        String temp = data.getString("type");
        if(temp != null) {
            type = RenderType.valueOf(temp);
        } else {
            type = RenderType.ALWAYS;
        }
        final String modelName = data.getString("modelName");
        final String modelClass_string = data.getString("modelClass");
        final Class<?> modelClass = SerializationUtil.classForName(modelClass_string);
        if(modelClass != null && AdvancedModel.class.isAssignableFrom(modelClass)) {
            Logger.log("Toll!: " + modelClass.getName());
            try {
                Object object = modelClass.getMethod("byName", String.class).invoke(modelClass.newInstance(), modelName);
                Logger.log("Toller: " + object);
                if(object != null && object instanceof AdvancedModel) {
                    model = (AdvancedModel) object;
                    Logger.log("Am bestesteN: " + model.getMaterial().getRenderer());
                }
            } catch (Exception ex) {
                Logger.logErr("Error while getting advanced model: " + ex, ex);
            }
        } else {
            Logger.log("Nicht Toll: " + AdvancedModel.class.isAssignableFrom(modelClass));
        }
        return this;
    }

}
