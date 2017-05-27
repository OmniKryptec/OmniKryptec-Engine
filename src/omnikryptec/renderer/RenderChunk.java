package omnikryptec.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.entity.Entity;
import omnikryptec.entity.GameObject;
import omnikryptec.logger.Logger;
import omnikryptec.main.Scene;
import omnikryptec.model.Material;
import omnikryptec.model.TexturedModel;

public class RenderChunk {

    private static int WIDTH = 128;
    private static int HEIGHT = 128;
    private static int DEPTH = 128;

    public static int getWidth() {
        return WIDTH;
    }

    public static int getHeight() {
        return HEIGHT;
    }

    public static int getDepth() {
        return DEPTH;
    }

    private static final ArrayList<IRenderer> allrenderer = new ArrayList<>();

    public static final void cleanup(){
        for(int i = 0; i < allrenderer.size(); i++){
            allrenderer.get(i).cleanup();
        }
    }

    private final long x, y, z;
    private final Scene scene;

    public RenderChunk(long x, long y, long z, Scene scene) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.scene = scene;
    }

    private final Map<IRenderer, Map<TexturedModel, List<Entity>>> chunk = new HashMap<>();
    private final ArrayList<GameObject> other = new ArrayList<>();

    private Entity tmp;
    private IRenderer tmpr;

    public void addGameObject(GameObject g) {
        if(g != null) {
            if(g instanceof Entity) {
                tmp = (Entity) g;
                TexturedModel tm = null;
                Material m = null;
                if((tm = tmp.getTexturedModel()) != null) {
                    if((m = tm.getMaterial()) != null) {
                        if((tmpr = m.getRenderer()) != null) {
                            if(!allrenderer.contains(tmpr)){
                                allrenderer.add(tmpr);
                            }
                            Map<TexturedModel, List<Entity>> map = chunk.get(tmpr);
                            if(map == null) {
                                map = new HashMap<>();
                                chunk.put(tmpr, map);
                            }
                            List<Entity> list = map.get(tm);
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
            } else {
                other.add(g);
            }
            g.setMyChunk(this);
        }
    }

    public GameObject removeGameObject(GameObject g) {
        if(g != null) {
            if(g instanceof Entity) {
                tmp = (Entity) g;
                TexturedModel tm = null;
                Material m = null;
                if((tm = tmp.getTexturedModel()) != null) {
                    if((m = tm.getMaterial()) != null) {
                        if((tmpr = m.getRenderer()) != null) {
                            Map<TexturedModel, List<Entity>> map = chunk.get(tmpr);
                            if(map != null) {
                                List<Entity> list = map.get(tm);
                                if(list != null) {
                                    list.remove(tmp);
                                    if(list.isEmpty()) {
                                        map.remove(tm);
                                    }
                                    if(map.isEmpty()) {
                                        chunk.remove(tmpr);
                                    }
                                    tmp.delete();
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
            } else {
                other.remove(g);
            }
            //g.setMyChunk(null);
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

    public static enum Render {
        All,
        EvElse,
        OnlThis;
    }

    private final IRenderer[] empty_array = new IRenderer[] {null};

    public void frame(Render type, IRenderer... rend) {
        if(rend == null || rend.length == 0){
            rend = empty_array;
        }
        for(IRenderer r : chunk.keySet()){
            if(r != null && (type == Render.All || (type == Render.OnlThis && contains(rend, r)) || (type == Render.EvElse && !contains(rend, r)))) {
                r.render(scene, chunk.get(r));
            }
        }
        for(GameObject g : other){
            if(g != null && g.isActive()) {
                g.doLogic();
                g.checkChunkPos();
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

}
