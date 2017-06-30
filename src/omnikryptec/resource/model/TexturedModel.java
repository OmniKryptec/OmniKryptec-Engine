package omnikryptec.resource.model;

import java.util.ArrayList;
import java.util.HashMap;

import omnikryptec.resource.texture.Texture;
import omnikryptec.test.saving.DataMap;

public class TexturedModel implements AdvancedModel {
    
    public static final HashMap<String, ArrayList<TexturedModel>> texturedModels = new HashMap<>();

    private String name;
    private Model model;
    private Texture texture;
    private Material material;
    
    public TexturedModel() {
    }

    public TexturedModel(String name, Model model, Texture texture) {
        this(name, model, texture, new Material());
    }

    public TexturedModel(String name, Model model, Texture texture, Material material) {
        this.name = name;
        this.model = model;
        this.texture = texture;
        this.material = material;
        ArrayList<TexturedModel> tms = texturedModels.get(name);
        if(tms == null) {
            tms = new ArrayList<>();
            texturedModels.put(name, tms);
        }
        tms.add(this);
    }

    @Override
    public final Model getModel() {
        return model;
    }

    @Override
    public final Texture getTexture() {
        return texture;
    }

    @Override
    public final Material getMaterial() {
        return material;
    }

    public final TexturedModel setMaterial(Material m) {
        this.material = m;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }
    
    /**
     * Deletes this AnimatedModel
     *
     * @return A reference to this AnimatedModel
     */
    @Override
    public final TexturedModel delete() {
        model.getVao().delete();
        deleteAll(true);
        return this;
    }
    
    private final TexturedModel deleteAll(boolean all) {
        ArrayList<TexturedModel> tms = (ArrayList<TexturedModel>) texturedModels.get(name).clone();
        if(tms != null) {
            tms.remove(this);
            if(tms.isEmpty()) {
                texturedModels.remove(name);
            } else if(all) {
                tms.stream().forEach((am) ->  {
                    am.deleteAll(false);
                });
            }
        }
        return this;
    }
    
    @Override
    public final TexturedModel copy() {
        return new TexturedModel(name, model, texture, material);
    }
    
    public static final TexturedModel byName(String name) {
        final ArrayList<TexturedModel> tms = texturedModels.get(name);
        if(tms != null && !tms.isEmpty()) {
            return tms.get(0).copy();
        }
        return null;
    }

    @Override
    public DataMap toDataMap(DataMap data) {
        return data;
    }
    
    @Override
    public TexturedModel fromDataMap(DataMap data) {
        return this;
    }

}
