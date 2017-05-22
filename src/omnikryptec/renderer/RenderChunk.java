package omnikryptec.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.main.Scene;
import omnikryptec.storing.Entity;
import omnikryptec.storing.GameObject;
import omnikryptec.storing.TexturedModel;

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

	private static List<IRenderer> allrenderer = new ArrayList<>();
	
	public static void cleanup(){
		for(int i=0; i<allrenderer.size(); i++){
			allrenderer.get(i).cleanup();
		}
	}
	
	private long x, y, z;
	private Scene scene;
	
	public RenderChunk(long x, long y, long z, Scene s) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.scene = s;
	}

	private Map<IRenderer, Map<TexturedModel, List<Entity>>> chunk = new HashMap<>();
	private List<GameObject> other = new ArrayList<>();

	private Entity tmp;
	private IRenderer tmpr;

	public void addGameObject(GameObject g) {
		if (g != null) {
			if (g instanceof Entity) {
				tmp = (Entity) g;
				if ((tmpr = tmp.getTexturedModel().getMaterial().getRenderer()) != null) {
					if(!allrenderer.contains(tmpr)){
						allrenderer.add(tmpr);
					}
					if (!chunk.containsKey(tmpr)) {
						chunk.put(tmpr, new HashMap<>());
					}
					if (!chunk.get(tmpr).containsKey(tmp.getTexturedModel())) {
						chunk.get(tmpr).put(tmp.getTexturedModel(), new ArrayList<>());
					}
					chunk.get(tmpr).get(tmp.getTexturedModel()).add(tmp);
				} else if (Logger.isDebugMode()) {
					Logger.log("IRenderer is null", LogLevel.WARNING);
				}
			} else {
				other.add(g);
			}
			g.setMyChunk(this);
		}
	}

	public GameObject removeGameObject(GameObject g) {
		if (g != null) {
			if (g instanceof Entity) {
				tmp = (Entity) g;
				if ((tmpr = tmp.getTexturedModel().getMaterial().getRenderer()) != null) {
					chunk.get(tmpr).get(tmp.getTexturedModel()).remove(tmp);
					if (chunk.get(tmpr).get(tmp.getTexturedModel()).isEmpty()) {
						chunk.get(tmpr).remove(tmp.getTexturedModel());
					}
					if (chunk.get(tmpr).isEmpty()) {
						chunk.remove(tmpr);
					}
				} else if (Logger.isDebugMode()) {
					Logger.log("IRenderer is null", LogLevel.WARNING);
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

	public static enum Render{
		All, EvElse, OnlThis;
	}
	
	private final IRenderer[] empty_array = new IRenderer[]{null};
	
	public void frame(Render type, IRenderer ...rend) {
		if(rend==null||rend.length==0){
			rend = empty_array;
		}

		for(IRenderer r : chunk.keySet()){
			if(r!=null&&(type==Render.All||(type==Render.OnlThis&&contains(rend, r))||(type==Render.EvElse&&!contains(rend, r)))){
				r.render(scene, chunk.get(r));
			}
		}
		for(int i=0; i<other.size(); i++){
			if(other.get(i)!=null&&other.get(i).isActive()){
				other.get(i).doLogic();
				other.get(i).checkChunkPos();
			}
		}
	}

	private boolean contains(Object[] array, Object obj){
		for(int i=0; i<array.length; i++){
			if(array[i]==obj){
				return true;
			}
		}
		return false;
	}
	
	public Scene getScene() {
		return scene;
	}


}
