package omnikryptec.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import omnikryptec.entity.Entity;
import omnikryptec.entity.GameObject;
import omnikryptec.entity.Light;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene;
import omnikryptec.model.AdvancedModel;
import omnikryptec.model.Material;
import omnikryptec.test.saving.DataMap;
import omnikryptec.test.saving.DataMapSerializable;
import omnikryptec.util.SerializationUtil;

public class RenderChunk implements DataMapSerializable {

    private static int WIDTH = OmniKryptecEngine.instance().getDisplayManager().getSettings().getChunkWidth();
    private static int HEIGHT = OmniKryptecEngine.instance().getDisplayManager().getSettings().getChunkHeight();
    private static int DEPTH = OmniKryptecEngine.instance().getDisplayManager().getSettings().getChunkDepth();

    /**
     * changing at runtime may cause errors
     *
     * @param width
     * @param height
     * @param depth
     */
    public static void setSize(int width, int height, int depth) {
        WIDTH = width;
        HEIGHT = height;
        DEPTH = depth;
    }

    public static int getWidth() {
        return WIDTH;
    }

    public static int getHeight() {
        return HEIGHT;
    }

    public static int getDepth() {
        return DEPTH;
    }

    public static final void cleanup() {

    }

    private long x, y, z;
    private final Scene scene;
    
    public RenderChunk() {
        this(0, 0, 0, null);
    }

    public RenderChunk(long x, long y, long z, Scene scene) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.scene = scene;
    }

    private final RenderMap<Renderer, RenderMap<AdvancedModel, List<Entity>>> chunk = new RenderMap<>(Renderer.class);
    private final ArrayList<GameObject> other = new ArrayList<>();
    private final List<Light> deferred_lights = new ArrayList<>();
    private final List<Light> forward_lights = new ArrayList<>();

    private Entity tmp;
    private Renderer tmpr;
    private RenderMap<AdvancedModel, List<Entity>> map;
    private List<Entity> list;
    private Material m;
    private AdvancedModel am;

    private Light tmpl;

    public void addGameObject(GameObject g) {
        if (g != null) {
            if (g instanceof Entity) {
                tmp = (Entity) g;
                am = null;
                m = null;
                if ((am = tmp.getAdvancedModel()) != null) {
                    if ((m = am.getMaterial()) != null) {
                        if ((tmpr = m.getRenderer()) != null) {
                            map = chunk.get(tmpr);
                            if (map == null) {
                                map = new RenderMap<>(AdvancedModel.class);
                                chunk.put(tmpr, map);
                            }
                            list = map.get(am);
                            if (list == null) {
                                list = new ArrayList<>();
                                map.put(am, list);
                            }
                            list.add(tmp);
                        } else if (Logger.isDebugMode()) {
                            Logger.log("IRenderer is null", LogLevel.WARNING);
                        }
                    } else if (Logger.isDebugMode()) {
                        Logger.log("Material is null", LogLevel.WARNING);
                    }
                } else if (Logger.isDebugMode()) {
                    Logger.log("TexturedModel is null", LogLevel.WARNING);
                }
            } else if (g instanceof Light) {
                tmpl = (Light) g;
                if ((tmpl.getShader()) != null) {
                    deferred_lights.add(tmpl);
                } else {
                    forward_lights.add(tmpl);
                }
            } else {
                other.add(g);
            }
            g.setMyChunk(this);
        }
    }

    public GameObject removeGameObject(GameObject g, boolean delete) {
        if (g != null) {
            if (g instanceof Entity) {
                tmp = (Entity) g;
                am = null;
                m = null;
                if ((am = tmp.getAdvancedModel()) != null) {
                    if ((m = am.getMaterial()) != null) {
                        if ((tmpr = m.getRenderer()) != null) {
                            map = chunk.get(tmpr);
                            if (map != null) {
                                list = map.get(am);
                                if (list != null) {
                                    list.remove(tmp);
                                    if (list.isEmpty()) {
                                        map.remove(am);
                                    }
                                    if (map.isEmpty()) {
                                        chunk.remove(tmpr);
                                    }
                                } else if (Logger.isDebugMode()) {
                                    Logger.log("List for Entities is null", LogLevel.WARNING);
                                }
                            } else if (Logger.isDebugMode()) {
                                Logger.log("Map for TexturedModel and Entities is null", LogLevel.WARNING);
                            }
                        } else if (Logger.isDebugMode()) {
                            Logger.log("IRenderer is null", LogLevel.WARNING);
                        }
                    } else if (Logger.isDebugMode()) {
                        Logger.log("Material is null", LogLevel.WARNING);
                    }
                } else if (Logger.isDebugMode()) {
                    Logger.log("TexturedModel is null", LogLevel.WARNING);
                }
            } else if (g instanceof Light) {
                tmpl = (Light) g;
                if ((tmpl.getShader()) != null) {
                    deferred_lights.remove(tmpl);
                } else {
                    forward_lights.remove(tmpl);
                }
            } else {
                other.remove(g);
            }
            if(delete){
            	g.deleteOperation();
            }
        }
        return g;
    }

    public long getChunkX() {
        return x;
    }

    public long getChunkY() {
        return y;
    }

    public long getChunkZ() {
        return z;
    }

    public static enum AllowedRenderer {
        All, EvElse, OnlThis;
    }

    private final Renderer[] empty_array = new Renderer[]{null};
    private Renderer r;
    private GameObject g;
    private long vertcount=0;
    
    public long frame(float maxExpenLvl, float minexplvl, boolean onlyRender, AllowedRenderer type, Renderer... rend) {
        if (rend == null || rend.length == 0) {
            rend = empty_array;
        }
        vertcount = 0;
        for (Renderer keysArray : chunk.keysArray()) {
            r = keysArray;
            if (r != null && r.expensiveLevel() <= maxExpenLvl && r.expensiveLevel() >= minexplvl
                    && (type == AllowedRenderer.All || (type == AllowedRenderer.OnlThis && contains(rend, r))
                    || (type == AllowedRenderer.EvElse && !contains(rend, r)))) {
                vertcount += r.render(scene, chunk.get(r), onlyRender);
            }
        }
        if (!onlyRender) {
            for (int i = 0; i < other.size(); i++) {
                g = other.get(i);
                if (g != null && g.isActive()) {
                    g.doLogic0();
                }
            }
            if(type == AllowedRenderer.All){
            	toreturnf.clear();
            	for(int i=0; i<forward_lights.size(); i++){
            		if(forward_lights.get(i).isActive()){
            			toreturnf.add(tmpl);
            		}
            	}
            	toreturnd.clear();
            	for(int i=0; i<deferred_lights.size(); i++){
            		if(deferred_lights.get(i).isActive()){
            			toreturnd.add(tmpl);
            		}
            	}
            }
        }
        return vertcount;
    }

    private boolean contains(Object[] array, Object obj) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == obj) {
                return true;
            }
        }
        return false;
    }

    public Scene getScene() {
        return scene;
    }
    
    private final List<Light> toreturnd = new ArrayList<>();
    public List<Light> getDeferredLights() {
        return toreturnd;
    }

    private final List<Light> toreturnf = new ArrayList<>();
    public List<Light> getForwardLights() {
        return toreturnf;
    }
    
    public List<Entity> getEntities() {
        final ArrayList<Entity> entities = new ArrayList<>();
        other.stream().filter((gameObject) -> gameObject instanceof Entity).forEach((entity) -> {
            entities.add((Entity) entity);
        });
        for(Renderer renderer : chunk.keysArray()) {
            RenderMap<AdvancedModel, List<Entity>> temp = chunk.get(renderer);
            for(AdvancedModel advancedModel : temp.keysArray()) {
                entities.addAll(temp.get(advancedModel));
            }
        }
        return entities;
    }

    @Override
    public DataMap toDataMap(DataMap data) {
        data.put("name", getName());
        final HashMap<Class<?>, ArrayList<Entity>> classesEntities = new HashMap<>();
        final HashMap<String, ArrayList<String>> chunk_renderer_entities = new HashMap<>();
        for(Renderer renderer : chunk.keysArray()) {
            RenderMap<AdvancedModel, List<Entity>> rendererModels = chunk.get(renderer);
            for(AdvancedModel advancedModel : rendererModels.keysArray()) {
                final List<Entity> entities = rendererModels.get(advancedModel);
                ArrayList<Entity> listTemp = classesEntities.get(renderer.getClass());
                if(listTemp == null) {
                    listTemp = new ArrayList<>();
                    classesEntities.put(renderer.getClass(), listTemp);
                }
                for(Entity entity : entities) {
                    listTemp.add(entity);
                }
            }
        }
        classesEntities.keySet().stream().forEach((c) -> {
            final ArrayList<String> names = new ArrayList<>();
            classesEntities.get(c).stream().forEach((entity) -> {
                names.add(entity.getName());
            });
            chunk_renderer_entities.put(c.getName(), names);
        });
        classesEntities.clear();
        data.put("chunk_renderer_entities", chunk_renderer_entities);
        final HashMap<Class<?>, ArrayList<GameObject>> classesOthers = SerializationUtil.gameObjectsToClassesGameObjects(other);
        final HashMap<Class<?>, ArrayList<String>> other_gameObjects = new HashMap<>();
        classesOthers.keySet().stream().forEach((c) -> {
            final ArrayList<String> names = new ArrayList<>();
            classesOthers.get(c).stream().forEach((gameObject) -> {
                names.add(gameObject.getName());
            });
            other_gameObjects.put(c, names);
        });
        classesOthers.clear();
        data.put("other_classes_gameObjects", other_gameObjects);
        return data;
    }

    public static RenderChunk newInstanceFromDataMap(DataMap data) {
        if(data == null) {
            return null;
        }
        return new RenderChunk().fromDataMap(data);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public RenderChunk fromDataMap(DataMap data) {
        if(data == null) {
            return this;
        }
        Vector3f temp_v = SerializationUtil.stringToVector3f(data.getString("name"));
        if(temp_v != null) {
            x = (long) temp_v.x;
            y = (long) temp_v.y;
            z = (long) temp_v.z;
        }
        other.clear();
        final Map<Class, Object> other_classes_gameObjects = data.getMap("other_classes_gameObjects", Class.class, Object.class);
        if(other_classes_gameObjects != null) {
            other_classes_gameObjects.keySet().stream().forEach((c) -> {
                final Object object = other_classes_gameObjects.get(c);
                if(object != null && object instanceof List) {
                    ((List) object).stream().forEach((name) -> {
                        try {
                            Object gameObject = c.getDeclaredMethod("byName", Class.class, String.class).invoke(c, "" + name);
                            if(gameObject != null && gameObject.getClass() == c) {
                                other.add((GameObject) gameObject);
                            }
                        } catch (Exception ex) {
                            Logger.logErr("Error while loading GameObject to RenderChunk: " + ex, ex);
                        }
                    });
                }
            });
            other_classes_gameObjects.clear();
        }
        for(Renderer renderer : chunk.keysArray()) {
            chunk.remove(renderer);
        }
        final Map<String, Object> chunk_renderer_entities = data.getMap("chunk_renderer_entities", String.class, Object.class);
        if(chunk_renderer_entities != null) {
            chunk_renderer_entities.keySet().stream().forEach((c) -> {
                final RenderMap<AdvancedModel, List<Entity>> rendererModels = new RenderMap<>(AdvancedModel.class);
                final Object object = chunk_renderer_entities.get(c);
                if(object != null && object instanceof List) {
                    ((List) object).stream().forEach((name) -> {
                        try {
                            Entity entity = GameObject.byName(Entity.class, "" + name, false);
                            if(entity != null) {
                                List<Entity> entities = rendererModels.get(entity.getAdvancedModel());
                                if(entities == null) {
                                    entities = new ArrayList<>();
                                    rendererModels.put(entity.getAdvancedModel(), entities);
                                }
                                entities.add(entity);
                            }
                        } catch (Exception ex) {
                            Logger.logErr("Error while loading Entity to RenderChunk: " + ex, ex);
                        }
                    });
                }
                if(!rendererModels.isEmpty()) {
                    chunk.put(RendererRegistration.byClass(SerializationUtil.classForName(c)), rendererModels);
                }
            });
            chunk_renderer_entities.clear();
        }
        return this;
    }

    @Override
    public String getName() {
        return SerializationUtil.vector3fToString(new Vector3f(x, y, z));
    }

}
