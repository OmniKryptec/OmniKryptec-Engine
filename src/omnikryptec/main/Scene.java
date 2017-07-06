package omnikryptec.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import omnikryptec.display.DisplayManager;
import omnikryptec.gameobject.gameobject.Camera;
import omnikryptec.gameobject.gameobject.Entity;
import omnikryptec.gameobject.gameobject.GameObject;
import omnikryptec.gameobject.gameobject.Light;
import omnikryptec.gameobject.particles.ParticleMaster;
import omnikryptec.physics.JBulletPhysicsWorld;
import omnikryptec.physics.PhysicsWorld;
import omnikryptec.renderer.RenderChunk;
import omnikryptec.renderer.RenderChunk.AllowedRenderer;
import omnikryptec.renderer.Renderer;
import omnikryptec.test.saving.DataMap;
import omnikryptec.test.saving.DataMapSerializable;
import omnikryptec.util.Color;
import omnikryptec.util.Instance;
import omnikryptec.util.PhysicsUtil;
import omnikryptec.util.logger.Logger;
import omnikryptec.util.logger.LogLevel;

public class Scene implements DataMapSerializable {

	public static enum FrameState{
		NULL,RENDERING,LOGIC;
	}
	
    private String name;
    private final Map<String, RenderChunk> scene = new HashMap<>();
    private Camera cam;
    private long cox = Instance.getGameSettings().getChunkRenderOffsetX(),
            coy = Instance.getGameSettings().getChunkRenderOffsetY(),
            coz = Instance.getGameSettings().getChunkRenderOffsetZ();
    private PhysicsWorld physicsWorld = null;
    private final List<Light> lights = new ArrayList<>();

    private RenderChunk global = new RenderChunk(0, 0, 0, this);

    private Color clearcolor = new Color(0, 0, 0, 0);
    private Color ambientlight = new Color(0, 0, 0, 0);

    /* Temp Variables */
    private String tmp;
    private long cx, cy, cz;
    private RenderChunk tmpc;
    private long vertcount = 0;

    private FrameState state = FrameState.NULL;
    
    public Scene() {
        this("", null);
    }

    public Scene(String name, Camera cam) {
        this.cam = cam;
        this.name = name;
    }

    public FrameState getState(){
    	return state;
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

    private double logictime = 0;
    private double tmptime2 = 0;
    public final void logic(boolean particles){
	    state = FrameState.LOGIC;
    	tmptime2 = DisplayManager.instance().getCurrentTime();
	    if(isUsingPhysics()){ 
	    	physicsWorld.stepSimulation();
	    }
	    if(Instance.getGameSettings().usesRenderChunking()){
		    cx = cam.getChunkX();
		    cy = cam.getChunkY();
		    cz = cam.getChunkZ();
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
        cam.doLogic();
        doLogic();
        logictime = DisplayManager.instance().getCurrentTime() - tmptime2;
        if(particles){
	    	ParticleMaster.instance().logic(cam);
        }
        state = FrameState.NULL;
    }
    
    private double rendertime = 0;
    private double tmptime = 0;
    
    public final long render(float maxexpenlvl, float minexplvl, AllowedRenderer info, boolean particles,
            Renderer... re) {
        state = FrameState.RENDERING;
    	tmptime = DisplayManager.instance().getCurrentTime();
    	lights.clear();
        lights.addAll(global.getImportantLights());
        vertcount = 0;
        if (Instance.getGameSettings().usesRenderChunking()) {
            cx = cam.getChunkX();
            cy = cam.getChunkY();
            cz = cam.getChunkZ();
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
                            vertcount += tmpc.render(maxexpenlvl, minexplvl, info, re);
                        }
                    }
                }
            }
        }
        vertcount += global.render(maxexpenlvl, minexplvl, info, re);
        rendertime = DisplayManager.instance().getCurrentTime() - tmptime;
        if(particles){
        	ParticleMaster.instance().render(cam);
        }
        state = FrameState.NULL;
        return vertcount;
    }

    /**
     * without particles
     * @return
     */
    public final double getRenderTimeMS(){
    	return rendertime;
    }
    
    public final double getLogicTimeMS(){
    	return logictime;
    }
    
    
    /**
     * override this to do your scene logic
     */
    protected void doLogic() {
    }

    public final List<Light> getLights() {
        return lights;
    }

    public final Camera getCamera() {
        return cam;
    }

    public final Scene setCamera(Camera cam) {
        this.cam = cam;
        return this;
    }

    public final Scene setClearColor(float r, float g, float b) {
        return setClearColor(r, g, b, 1);
    }

    public final Scene setClearColor(float r, float g, float b, float a) {
        clearcolor.set(r, g, b, a);
        return this;
    }

    public final Scene setClearColor(Color f) {
        clearcolor = f;
        return this;
    }

    public final Color getClearColor() {
        return clearcolor;
    }

    public final PhysicsWorld getPhysicsWorld() {
        return physicsWorld;
    }

    public final Scene setPhysicsWorld(PhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
        return this;
    }

    public final Scene useDefaultPhysics() {
        return setPhysicsWorld(new JBulletPhysicsWorld(PhysicsUtil.createDefaultDynamicsWorld()));
    }

    public final boolean isUsingPhysics() {
        return physicsWorld != null;
    }

    public final Scene setName(String name) {
        this.name = name;
        return this;
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

    public static final Scene byName(String name) {
        if (OmniKryptecEngine.getInstance() != null) {
            for (Scene scene : OmniKryptecEngine.getInstance().getScenes()) {
                if (scene.getName() == null ? name == null : scene.getName().equals(name)) {
                    return scene;
                }
            }
            return null;
        } else {
            return null;
        }
    }

    @Override
    public DataMap toDataMap(DataMap data) {
        data.put("name", name);
        data.put("camera", (cam != null ? cam.toDataMap(new DataMap("camera")) : null));
        final List<Entity> entities = getEntities();
        Logger.log("SCENE: " + name + " ENTITIES: " + Arrays.toString(entities.toArray()));
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
        final Scene scene = byName(data.getString("name"));
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
            if (cam != null) {
                cam.fromDataMap(dataMap_temp);
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

    @Override
    public String getName() {
        return name;
    }

    public Scene setValuesFrom(Scene scene) {
        if (scene == null) {
            return this;
        }
        setName(scene.getName());
        return this;
    }

}
