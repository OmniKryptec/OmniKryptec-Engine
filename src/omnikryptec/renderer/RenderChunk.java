package omnikryptec.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import omnikryptec.audio.AudioSource;
import omnikryptec.component.AudioSourceComponent;

import omnikryptec.entity.Entity;
import omnikryptec.entity.GameObject;
import omnikryptec.light.Light;
import omnikryptec.light.LightPrepare;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene;
import omnikryptec.model.Material;
import omnikryptec.model.TexturedModel;

public class RenderChunk {

    private static int WIDTH = OmniKryptecEngine.instance().getDisplayManager().getSettings().getChunkWidth();
    private static int HEIGHT = OmniKryptecEngine.instance().getDisplayManager().getSettings().getChunkHeight();
    private static int DEPTH = OmniKryptecEngine.instance().getDisplayManager().getSettings().getChunkDepth();
    
    /**
     * changing at runtime may cause errors
     * @param width
     * @param height
     * @param depth
     */
    public static void setSize(int width, int height, int depth){
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


    public static final void cleanup(){
        
    }

    private final long x, y, z;
    private final Scene scene;

    public RenderChunk(long x, long y, long z, Scene scene) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.scene = scene;
    }

    private final RenderMap<IRenderer, RenderMap<TexturedModel, List<Entity>>> chunk = new RenderMap<>(IRenderer.class);
    private final ArrayList<GameObject> other = new ArrayList<>();
    private final Map<LightPrepare, List<Light>> lights = new HashMap<>();

    
    private Entity tmp;
    private IRenderer tmpr;
    private RenderMap<TexturedModel, List<Entity>> map;
    private List<Entity> list;
    private Material m;
    private TexturedModel tm;
    
    private Light tmpl;
    private LightPrepare lightpre;
    private List<Light> lightl;
    
    public void addGameObject(GameObject g) {
        if(g != null) {
            if(g instanceof Entity) {
                tmp = (Entity) g;
                tm = null;
                m = null;
                if((tm = tmp.getTexturedModel()) != null) {
                    if((m = tm.getMaterial()) != null) {
                        if((tmpr = m.getRenderer()) != null) {
                            map = chunk.get(tmpr);
                            if(map == null) {
                                map = new RenderMap<>(TexturedModel.class);
                                chunk.put(tmpr, map);
                            }
                            list = map.get(tm);
                            if(list == null) {
                                list = new ArrayList<>();
                                map.put(tm, list);
                            }
                            list.add(tmp);
                        } else if (Logger.isDebugMode()) {
                            Logger.log("IRenderer is null", LogLevel.WARNING);
                        }
                    } else if(Logger.isDebugMode()) {
                        Logger.log("Material is null", LogLevel.WARNING);
                    }
                } else if(Logger.isDebugMode()) {
                    Logger.log("TexturedModel is null", LogLevel.WARNING);
                }
            } else if(g instanceof Light){
            	tmpl = (Light)g;
            	if((lightpre = tmpl.getShader())!=null){
            		if((lightl = lights.get(lightpre))==null){
            			lights.put(lightpre, new ArrayList<>());
            		}
            		lights.get(lightpre).add(tmpl);
            	}
            } else{
                other.add(g);
            }
            g.setMyChunk(this);
        }
    }

    public GameObject removeGameObject(GameObject g, boolean delete) {
        if(g != null) {
            if(g instanceof Entity) {
                tmp = (Entity) g;
                tm = null;
                m = null;
                if((tm = tmp.getTexturedModel()) != null) {
                    if((m = tm.getMaterial()) != null) {
                        if((tmpr = m.getRenderer()) != null) {
                            map = chunk.get(tmpr);
                            if(map != null) {
                               list = map.get(tm);
                                if(list != null) {
                                    list.remove(tmp);
                                    if(list.isEmpty()) {
                                        map.remove(tm);
                                    }
                                    if(map.isEmpty()) {
                                        chunk.remove(tmpr);
                                    }
                                    if(delete){
                                    	tmp.deleteOperation();
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
                    } else if(Logger.isDebugMode()) {
                        Logger.log("Material is null", LogLevel.WARNING);
                    }
                } else if(Logger.isDebugMode()) {
                    Logger.log("TexturedModel is null", LogLevel.WARNING);
                }
            } else if(g instanceof Light){
            	tmpl = (Light) g;
            	if((lightpre = tmpl.getShader()) != null) {
                    if((lightl = lights.get(lightpre)) != null) {
                        lightl.remove(tmpl);
                        if(lightl.isEmpty()) {
                            lights.remove(lightpre);
                        }
                        if(delete) {
                            tmpl.deleteOperation();
                        }
                    }
            	}
            } else {
                other.remove(g);
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
        All,
        EvElse,
        OnlThis;
    }

    private final IRenderer[] empty_array = new IRenderer[] {null};
    private IRenderer r;
    private RenderMap<TexturedModel, List<Entity>> rm;
    private List<Entity> e;
    private GameObject g;
    
    public void frame(float maxExpenLvl, float minexplvl, AllowedRenderer type, IRenderer... rend) {
        if(rend == null || rend.length == 0) {
            rend = empty_array;
        }
        for(IRenderer keysArray : chunk.keysArray()) {
            r = keysArray;
            if(r != null && r.expensiveLevel() <= maxExpenLvl && r.expensiveLevel() >= minexplvl && (type == AllowedRenderer.All || (type == AllowedRenderer.OnlThis && contains(rend, r)) || (type == AllowedRenderer.EvElse && !contains(rend, r)))) {
                rm = chunk.get(r);
                r.render(scene, rm);
                if(scene.isUsingPhysics()) {
                    final boolean paused = scene.getPhysicsWorld().isSimulationPaused();
                    final float newDeltaPitch = scene.getPhysicsWorld().getSimulationSpeed() - 1.0F;
                    for(TexturedModel tm_ : rm.keysArray()) {
                        e = rm.get(tm_);
                        e.stream().forEach((entity) -> {
                            processAudioSourceComponents(entity, paused, newDeltaPitch);
                        });
                    }
                }
            }
        }
        for(int i = 0; i < other.size(); i++) {
            g = other.get(i);
            if(g != null && g.isActive()) {
                if(scene.isUsingPhysics()) {
                    final boolean paused = scene.getPhysicsWorld().isSimulationPaused();
                    final float newDeltaPitch = scene.getPhysicsWorld().getSimulationSpeed() - 1.0F;
                    processAudioSourceComponents(g, paused, newDeltaPitch);
                }
                g.doLogic0();
            }
        }
    }

    private boolean contains(Object[] array, Object obj){
        for(int i = 0; i < array.length; i++){
            if(array[i] == obj){
                return true;
            }
        }
        return false;
    }

    public Scene getScene() {
        return scene;
    }

    public Map<LightPrepare, List<Light>> getLights() {
        return lights;
    }
    
    private static final void processAudioSourceComponents(GameObject gameObject, boolean paused, float newDeltaPitch) {
        gameObject.getComponents(AudioSourceComponent.class).forEach((audioSourceComponent) -> {
            audioSourceComponent.getSources().stream().forEach((source) -> {
                if(paused && source.isPlaying()) {
                    source.pauseTemporarily();
                } else if(!paused && !source.isPlaying()) {
                    source.continuePlayingTemporarily();
                }
                source.setDeltaPitch(newDeltaPitch);
            });
        });
    }

}
