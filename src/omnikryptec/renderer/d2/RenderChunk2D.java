package omnikryptec.renderer.d2;

import java.util.ArrayList;

import omnikryptec.gameobject.GameObject2D;
import omnikryptec.gameobject.Light2D;
import omnikryptec.gameobject.Sprite;
import omnikryptec.main.AbstractScene2D;
import omnikryptec.main.GameObjectContainer;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.settings.GameSettings;

public class RenderChunk2D implements GameObjectContainer<GameObject2D>{

	private static int WIDTH=OmniKryptecEngine.instance().getDisplayManager().getSettings().getInteger(GameSettings.CHUNK_WIDTH_2D);
	private static int HEIGHT=OmniKryptecEngine.instance().getDisplayManager().getSettings().getInteger(GameSettings.CHUNK_HEIGHT_2D);
	
	public static int getWidth() {
		return WIDTH;
	}
	public static int getHeight() {
		return HEIGHT;
	}
	
	
	
	private AbstractScene2D scene;
	private long x,y;
	public final boolean isglobal;
	private final ArrayList<Sprite> chunkSprites = new ArrayList<>();
	private final ArrayList<GameObject2D> chunkOther = new ArrayList<>(); 
	private final ArrayList<Light2D> chunkLights = new ArrayList<>();
	
	//currently unused
//	private RenderChunk2D() {
//		this(0, 0, null);
//	}

	public RenderChunk2D(long x, long y, AbstractScene2D scene) {
		this(x, y, scene, false);
	}
	
	public RenderChunk2D(long x, long y, AbstractScene2D scene, boolean global) {
		this.x = x;
		this.y = y;
		this.scene = scene;
		this.isglobal = global;
	}
	
	@Override
	public void addGameObject(GameObject2D go) {
		if(go!=null) {
			if(go instanceof Sprite) {
				if(go instanceof Light2D) {
					chunkLights.add((Light2D)go);
				}else {
					chunkSprites.add((Sprite)go);
				}
			}else {
				chunkOther.add(go);
			}
			go.setRenderChunk2D(this);
		}
	}

	
	@Override
	public GameObject2D removeGameObject(GameObject2D go, boolean delete) {
		if(go instanceof Sprite) {
			chunkSprites.remove(go);
		}else {
			chunkOther.remove(go);
		}
		if(delete) {
			go.deleteOperation();
		}
		return go;
	}
	
	public AbstractScene2D getScene() {
		return scene;
	}
	
	public long getChunkX() {
		return x;
	}

	public long getChunkY() {
		return y;
	}
	
	public void logic() {
		for(int i=0; i<chunkOther.size(); i++) {
			chunkOther.get(i).doLogic();
		}
		for(int i=0; i<chunkLights.size(); i++) {
			chunkLights.get(i).doLogic();
		}
		for(int i=0; i<chunkSprites.size(); i++) {
			chunkSprites.get(i).doLogic();
		}
	}
	

	public ArrayList<Light2D> __getLights() {
		return chunkLights;
	}
	
	public ArrayList<Sprite> __getSprites(){
		return chunkSprites;
	}
}
