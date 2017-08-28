package omnikryptec.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.Entity;
import omnikryptec.gameobject.GameObject;
import omnikryptec.gameobject.Light;
import omnikryptec.renderer.RenderChunk;
import omnikryptec.renderer.RenderConfiguration;
import omnikryptec.renderer.Renderer;
import omnikryptec.test.saving.DataMap;
import omnikryptec.util.Color;
import omnikryptec.util.Instance;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public class Scene extends AbstractScene {

    private final Map<String, RenderChunk> scene = new HashMap<>();
    private long cox = Instance.getGameSettings().getChunkRenderOffsetX(),
            coy = Instance.getGameSettings().getChunkRenderOffsetY(),
            coz = Instance.getGameSettings().getChunkRenderOffsetZ();
    private final List<Light> lights = new ArrayList<>();

    private RenderChunk global = new RenderChunk(0, 0, 0, this);

    private Color ambientlight = new Color(0, 0, 0, 0);

    /* Temp Variables */
    private String tmp;
    private long cx, cy, cz;
    private RenderChunk tmpc;
    private long vertcount = 0;

    
    public Scene() {
        this("", null);
    }

    public Scene(String name, Camera cam) {
        super(name, cam);
    }

    
    public Scene setAmbientColor(float r, float g, float b) {
        ambientlight.set(r, g, b);
        return this;
    }

    public Scene setChunkOffsets(long xo, long yo, long zo) {
        this.cox = xo;
        this.coy = yo;
        this.coz = zo;
        return this;
    }

    public final boolean addGameObject(GameObject g) {
        if (g != null) {
            if (g instanceof Camera && Logger.isDebugMode()) {
                Logger.log("A Camera should not be added as a GameObject!", LogLevel.WARNING);
                return false;
            }
            if (g.isGlobal() || !Instance.getGameSettings().usesRenderChunking()) {
                global.addGameObject(g);
                return true;
            }
            tmp = xyzToString(g.getChunkX(), g.getChunkY(), g.getChunkZ());
            if (!scene.containsKey(tmp)) {
                scene.put(tmp, new RenderChunk(g.getChunkX(), g.getChunkY(), g.getChunkZ(), this));
            }
            scene.get(tmp).addGameObject(g);
            return true;
        } else {
            return false;
        }
    }

    public final GameObject removeGameObject(GameObject g) {
        return removeGameObject(g, true);
    }

    public final GameObject removeGameObject(GameObject g, boolean delete) {
        if (g != null) {
            if (g.getMyChunk() != null) {
                g.getMyChunk().removeGameObject(g, delete);
            } else {
                tmp = xyzToString(g.getChunkX(), g.getChunkY(), g.getChunkZ());
                scene.get(tmp).removeGameObject(g, delete);
                g.deleteOperation();
            }
        }
        return g;
    }

    public final void logic(){
	    if(Instance.getGameSettings().usesRenderChunking()){
		    cx = getCamera().getChunkX();
		    cy = getCamera().getChunkY();
		    cz = getCamera().getChunkZ();
		    for (long x = -cox + cx; x <= cox + cx; x++) {
		        for (long y = -coy + cy; y <= coy + cy; y++) {
		            for (long z = -coz + cz; z <= coz + cz; z++) {
		                if ((tmpc = scene.get(xyzToString(x, y, z))) != null) {
		                    tmpc.logic();
		                }
		            }
		        }
		    }
		}
        global.logic();
        doLogic();
    }
    
    
    public final long render(RenderConfiguration config) {
    	lights.clear();
        lights.addAll(global.getImportantLights());
        vertcount = 0;
        if (Instance.getGameSettings().usesRenderChunking()) {
            cx = getCamera().getChunkX();
            cy = getCamera().getChunkY();
            cz = getCamera().getChunkZ();
            for (long x = -cox + cx; x <= cox + cx; x++) {
                for (long y = -coy + cy; y <= coy + cy; y++) {
                    for (long z = -coz + cz; z <= coz + cz; z++) {
                        if ((tmpc = scene.get(xyzToString(x, y, z))) != null) {
                            lights.addAll(tmpc.getImportantLights());
                        }
                    }
                }
            }
            for (long x = -cox + cx; x <= cox + cx; x++) {
                for (long y = -coy + cy; y <= coy + cy; y++) {
                    for (long z = -coz + cz; z <= coz + cz; z++) {
                        if ((tmpc = scene.get(xyzToString(x, y, z))) != null) {
                            vertcount += tmpc.render(config);
                        }
                    }
                }
            }
        }
        vertcount += global.render(config);
        return vertcount;
    }
 
    /**
     * override this to do your scene logic
     */
    protected void doLogic() {
    }

    public final List<Light> getLights() {
        return lights;
    }


    public final List<Entity> getEntities() {
        final List<Entity> entities = global.getEntities();
        scene.values().stream().forEach((chunk) -> {
            entities.addAll(chunk.getEntities());
        });
        return entities;
    }

    private static String xyzToString(long x, long y, long z) {
        return x + ":" + y + ":" + z;
    }

    public Color getAmbient() {
        return ambientlight;
    }

    

    @Override
    public DataMap toDataMap(DataMap data) {
        data.put("name", getName());
        data.put("camera", (getCamera() != null ? getCamera().toDataMap(new DataMap("camera")) : null));
        final List<Entity> entities = getEntities();
        Logger.log("SCENE: " + getName() + " ENTITIES: " + Arrays.toString(entities.toArray()));
        final List<String> entityNames = new ArrayList<>();
        entities.stream().forEach((entity) -> {
            entityNames.add(entity.getName());
        });
        entities.clear();
        data.put("entityNames", entityNames);
        /*
        data.put("renderChunk_global", (global != null ? global.toDataMap(new DataMap("renderChunk_global")) : null));
        int i = 0;
        for(String g : scene.keySet()) {
            data.put("renderChunk_" + i, scene.get(g).toDataMap(new DataMap(g)));
            i++;
        }
         */
        return data;
    }

    public static Scene newInstanceFromDataMap(DataMap data) {
        if (data == null) {
            return null;
        }
        Scene scene;
        try {
        	scene = (Scene) byName(data.getString("name"));
        }catch(Exception ex) {
        	scene = null;
        	Logger.logErr("Can't cast to Scene (byName returned wrong type)", ex);
        }
        return (scene != null ? scene : new Scene()).fromDataMap(data);
    }

    @Override
    public Scene fromDataMap(DataMap data) {
        if (data == null) {
            return this;
        }
        setName(data.getString("name"));
        DataMap dataMap_temp = data.getDataMap("camera");
        if (dataMap_temp != null) {
            if (getCamera() != null) {
                getCamera().fromDataMap(dataMap_temp);
            } else {
                Object temp = Camera.newInstanceFromDataMap(dataMap_temp);
                if (temp != null && temp instanceof Camera) {
                    setCamera((Camera) temp);
                }
            }
        } else {
            Logger.log("Camera is null!");
            setCamera(null);
        }
        final List<Entity> entities = getEntities();
        final List<String> entityNames = data.getList("entityNames", String.class);
        if (entityNames == null) {
            Logger.log("Entity name list is null!", LogLevel.WARNING);
            return this;
        }
        for (String entitiyName : entityNames) {
            try {
                final Entity entity = Entity.byName(Entity.class, entitiyName, false);
                if (entity != null) {
                    for (Entity e : entities) {
                        if (e.getName().equals(entity.getName())) {
                            e.setValuesFrom(entity);
                            break;
                        }
                    }
                    Logger.log("Added: " + entity);
                    addGameObject(entity);
                } else {
                    Logger.log("Entity not found!", LogLevel.WARNING);
                }
            } catch (Exception ex) {
                Logger.logErr("Error while adding entity: " + ex, ex);
            }
        }
        /*
        dataMap_temp = data.getDataMap("renderChunk_global");
        if(dataMap_temp != null) {
            if(global != null) {
                global.fromDataMap(dataMap_temp);
            } else {
                Object temp = RenderChunk.newInstanceFromDataMap(dataMap_temp);
                if(temp != null && temp instanceof RenderChunk) {
                    global = (RenderChunk) temp;
                }
            }
        } else {
            Logger.log("Global RenderChunk is null!");
            global = null;
        }
        int i = 0;
        while((dataMap_temp = data.getDataMap("renderChunk_" + i)) != null) {
            String name_temp = dataMap_temp.getString("name");
            RenderChunk chunk = scene.get(name_temp);
            if(chunk != null) {
                chunk.fromDataMap(dataMap_temp);
            } else {
                Object temp = RenderChunk.newInstanceFromDataMap(dataMap_temp);
                if(temp != null && temp instanceof RenderChunk) {
                    scene.put(name_temp, (RenderChunk) temp);
                }
            }
            i++;
        }
         */
        return this;
    }

    public Scene setValuesFrom(Scene scene) {
        if (scene == null) {
            return this;
        }
        setName(scene.getName());
        return this;
    }

}
