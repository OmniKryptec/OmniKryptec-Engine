package omnikryptec.gameobject;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import omnikryptec.animation.AnimatedModel;
import omnikryptec.display.DisplayManager;
import omnikryptec.resource.loader.ResourceLoader;
import omnikryptec.resource.model.AdvancedModel;
import omnikryptec.resource.model.Model;
import omnikryptec.resource.model.TexturedModel;
import omnikryptec.resource.texture.SimpleTexture;
import omnikryptec.test.saving.DataMap;
import omnikryptec.test.saving.DataMapSerializable;
import omnikryptec.util.Color;
import omnikryptec.util.Maths;
import omnikryptec.util.RenderUtil;
import omnikryptec.util.SerializationUtil;
import omnikryptec.util.logger.Logger;
import omnikryptec.util.logger.LogLevel;

public class Entity extends GameObject implements DataMapSerializable {
    
    private AdvancedModel model;
    private RenderType type = RenderType.ALWAYS;
    private Color color = new Color(1, 1, 1, 1);
    private boolean renderingEnabled=true;
    
    


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
        this.color = new Color(copy.color);
    }

    /**
     * sets the RenderType of this entity.
     * @see #getType()
     * @param type
     * @return this Entity
     */
    public Entity setRenderType(RenderType type) {
        this.type = type;
        return this;
    }

    public RenderType getType() {
        return type;
    }

    /**
     * sets the AdvancedModel of this Entity. if the AdvancedModel is changed at runtime, 
     * this method should be called to ensure that the right renderer will render this entity.
     * @see TexturedModel 
     * @see AnimatedModel
     * @param model
     * @return this Entity
     */
    public final Entity setAdvancedModel(AdvancedModel model) {
        this.model = model;
        //if renderer gets changed this entity must be treated differently 
        checkChunkPos(false);
        return this;
    }

    /**
     * the Advanced Model of this entity
     * @see #setAdvancedModel(AdvancedModel)
     * @return Advanced Model
     */
    public final AdvancedModel getAdvancedModel() {
        return model;
    }

    /**
     * sets the color of this Entity
     * @see #setColor(float, float, float, float)
     * @param c
     * @return this Entity
     */
    public Entity setColor(Color c) {
        this.color = c;
        return this;
    }

    /**
     * sets the color of this Entity.
     * a renderer might use this differently than others.
     * @param r [0,1]
     * @param g [0,1]
     * @param b [0,1]
     * @param a [0,1]
     * @return this Entity
     */
    public Entity setColor(float r, float g, float b, float a) {
        color.set(r, g, b, a);
        return this;
    }

    /**
     * the color of this Entity
     * @see #setColor(float, float, float, float)
     * @return a color
     */
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
        super.setValuesFrom(toCopy);
        color = toCopy.color.getClone();
        model = toCopy.model;
        type = toCopy.type;
        return this;
    }
    
    @Override
    public DataMap toDataMap(DataMap data) {
        DataMap data_temp = super.toDataMap(new DataMap("gameObject"));
        data.put(data_temp.getName(), data_temp);
        data.put("color", SerializationUtil.colorToString(color));
        data.put("type", type.name());
        if(model != null) {
            data.put("model", model.toDataMap(new DataMap(model.getName())));
        }
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
        Logger.log("Creating Entity from DataMap: " + data.getName());
        DataMap dataMap_temp = data.getDataMap("gameObject");
        super.fromDataMap(dataMap_temp);
        color = SerializationUtil.stringToColor(data.getString("color"));
        String temp = data.getString("type");
        if(temp != null) {
            type = RenderType.valueOf(temp);
        } else {
            type = RenderType.ALWAYS;
        }
        dataMap_temp = data.getDataMap("model");
        if(dataMap_temp != null) {
            final String modelName = dataMap_temp.getName();
            Logger.log("Loading AdvancedModel: " + modelName + ", for: " + this);
            try {
                model = ResourceLoader.getInstance().getData(AdvancedModel.class, modelName);
                if(model != null) {
                    model.fromDataMap(dataMap_temp);
                } else if(Logger.isDebugMode()) {
                    Logger.log("AdvancedModel is null!", LogLevel.WARNING);
                }
            } catch (Exception ex) {
                model = null;
                Logger.logErr("Error while setting up the advanced model: " + ex, ex);
            }
        } else if(Logger.isDebugMode()) {
            model = null;
            Logger.log("AdvancedModel DataMap is null!", LogLevel.WARNING);
        }
        return this;
    }

    
    public boolean isRenderingEnabled() {
		return renderingEnabled;
	}

	public void setRenderingEnabled(boolean renderingEnabled) {
		this.renderingEnabled = renderingEnabled;
	}
	

    
}
