package omnikryptec.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import omnikryptec.deferredlight.DeferredLightPrepare;
import omnikryptec.entity.Camera;
import omnikryptec.entity.GameObject;
import omnikryptec.entity.Light;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.physics.PhysicsWorld;
import omnikryptec.renderer.RenderChunk;
import omnikryptec.renderer.RenderChunk.AllowedRenderer;
import omnikryptec.renderer.Renderer;
import omnikryptec.util.Color;
import omnikryptec.test.saving.DataMap;
import omnikryptec.test.saving.DataMapSerializable;
import omnikryptec.util.PhysicsUtil;

public class Scene implements DataMapSerializable{

        private String name;
	private final Map<String, RenderChunk> scene = new HashMap<>();
	private Camera cam;
	private long cox = OmniKryptecEngine.getInstance().getDisplayManager().getSettings().getChunkRenderOffsetX(),
			coy = OmniKryptecEngine.getInstance().getDisplayManager().getSettings().getChunkRenderOffsetY(),
			coz = OmniKryptecEngine.getInstance().getDisplayManager().getSettings().getChunkRenderOffsetZ();
	private float[] clearcolor = { 0, 0, 0, 0 };
	private PhysicsWorld physicsWorld = null;
	private final Map<DeferredLightPrepare, List<Light>> deferred_rel_lights = new HashMap<>();
	private final List<Light> forward_rel_lights = new ArrayList<>();

	private RenderChunk global = new RenderChunk(0, 0, 0, this);
	
	private Color ambientlight = new Color(0, 0, 0, 0);
	
	/* Temp Variables */
	private String tmp;
	private long cx, cy, cz;
	private RenderChunk tmpc;
        
        public Scene() {
            this("", null);
        }
	
	public Scene(String name, Camera cam) {
            this.cam = cam;
            this.name = name;
	}
	
	public Scene setAmbientColor(float r, float g, float b){
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
			if(g.isGlobal()){
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

	public final Scene frame(float maxexpenlvl, float minexplvl, boolean onlyRender, AllowedRenderer info, Renderer... re) {
		deferred_rel_lights.clear();
		deferred_rel_lights.putAll(global.getDeferredLights());
		forward_rel_lights.clear();
		forward_rel_lights.addAll(global.getForwardLights());
		if (!onlyRender&&isUsingPhysics()) {
			physicsWorld.stepSimulation();
		}
		cx = cam.getChunkX();
		cy = cam.getChunkY();
		cz = cam.getChunkZ();
		for (long x = -cox + cx; x <= cox + cx; x++) {
			for (long y = -coy + cy; y <= coy + cy; y++) {
				for (long z = -coz + cz; z <= coz + cz; z++) {
					if ((tmpc = scene.get(xyzToString(x, y, z))) != null) {
						forward_rel_lights.addAll(tmpc.getForwardLights());
					}
				}
			}
		}
		for (long x = -cox + cx; x <= cox + cx; x++) {
			for (long y = -coy + cy; y <= coy + cy; y++) {
				for (long z = -coz + cz; z <= coz + cz; z++) {
					if ((tmpc = scene.get(xyzToString(x, y, z))) != null) {
						tmpc.frame(maxexpenlvl, minexplvl, onlyRender, info, re);
						deferred_rel_lights.putAll(tmpc.getDeferredLights());
					}
				}
			}
		}
		global.frame(maxexpenlvl, minexplvl, onlyRender, info, re);
		if(!onlyRender){
			cam.doLogic0();
			doLogic();
		}
		return this;
	}

	/**
	 * override this to do your scene logic
	 */
	protected void doLogic() {
	}

	public final List<Light> getDeferredRenderLights(DeferredLightPrepare usingShader) {
		return deferred_rel_lights.get(usingShader);
	}
	
	public final List<Light> getForwardRenderLights(){
		return forward_rel_lights;
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
		clearcolor = new float[] { r, g, b, a };
		return this;
	}

	public final Scene setClearColor(float[] f) {
		clearcolor = f;
		return this;
	}

	public final float[] getClearColor() {
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
		return setPhysicsWorld(new PhysicsWorld(PhysicsUtil.createDefaultDynamicsWorld()));
	}

	public final boolean isUsingPhysics() {
		return physicsWorld != null;
	}
        
        public final Scene setName(String name) {
            this.name = name;
            return this;
        }

	private static String xyzToString(long x, long y, long z) {
		return x + ":" + y + ":" + z;
	}

	public Color getAmbient() {
		return ambientlight;
	}
        
        public static final Scene byName(String name) {
            if(OmniKryptecEngine.getInstance() != null) {
                for(Scene scene : OmniKryptecEngine.getInstance().getScenes()) {
                    if(scene.getName() == null ? name == null : scene.getName().equals(name)) {
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
        return data;
    }

    public static Scene newInstanceFromDataMap(DataMap data) {
        if(data == null) {
            return null;
        }
        final Scene scene = byName(data.getString("name"));
        return (scene != null ? scene : new Scene()).fromDataMap(data);
    }
    
    @Override
    public Scene fromDataMap(DataMap data) {
        if(data == null) {
            return this;
        }
        setName(data.getString("name"));
        DataMap dataMap_temp = data.getDataMap("camera");
        if(dataMap_temp != null) {
            if(cam != null) {
                cam.fromDataMap(dataMap_temp);
            } else {
                Object temp = Camera.newInstanceFromDataMap(dataMap_temp);
                if(temp != null && temp instanceof Camera) {
                    setCamera((Camera) temp);
                }
            }
        } else {
            Logger.log("Camera is null!");
            setCamera(null);
        }
        return this;
    }
    
    @Override
    public String getName() {
        return name;
    }

    public Scene setValuesFrom(Scene scene) {
        if(scene == null) {
            return this;
        }
        setName(scene.getName());
        return this;
    }
    
}
