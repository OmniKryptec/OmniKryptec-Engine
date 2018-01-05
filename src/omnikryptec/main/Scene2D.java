package omnikryptec.main;

import java.util.HashMap;

import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.GameObject2D;
import omnikryptec.renderer.d2.DefaultRenderer2D;
import omnikryptec.renderer.d2.RenderChunk2D;
import omnikryptec.renderer.d2.Renderer2D;
import omnikryptec.settings.GameSettings;
import omnikryptec.util.Instance;
import omnikryptec.util.logger.Logger;

public class Scene2D extends AbstractScene2D{
	
	private HashMap<String, RenderChunk2D> scene = new HashMap<>();
	private RenderChunk2D global = new RenderChunk2D(0, 0, this, true);
	private Renderer2D renderer;

	private long cx,cy;
	private RenderChunk2D tmpc;
	private String tmp;
	
	public Scene2D() {
		this("", null);
	}
	
	public Scene2D(String name) {
		this(name, null);
	}
	
	public Scene2D(String name, Camera cam) {
		super(name, cam);
		setRenderer(new DefaultRenderer2D());
		init();
	}
	
	public Scene2D(String name, Camera cam, Renderer2D renderer) {
		this(name, cam);
		setRenderer(renderer);
		init();
	}
	
	private void init() {
		setAmbientColor(1, 1, 1);
	}
	

	
	public void setRenderer(Renderer2D renderer) {
		this.renderer = renderer;
	}
	
	
	@Override
	protected void logic() {
		if(Instance.getGameSettings().usesRenderChunking()) {
		    cx = getCamera().getTransform().getChunkX2D();
		    cy = getCamera().getTransform().getChunkY2D();
		    for (long x = -cox; x <= cox; x++) {
		        for (long y = -coy; y <= coy; y++) {
	                if ((tmpc = scene.get(xyToString(x + cx, y + cy))) != null) {
	                    tmpc.logic();
	                }
		        }
		    }
		}
        global.logic();
        update();
	}

	protected void update() {}

	@Override
	protected long render() {
		if(getCamera()==null) {
			return 0;
		}
		return renderer.render(this, global, getCamera().getTransform().getChunkX2D(), getCamera().getTransform().getChunkY2D(), cox, coy, scene);
	}

	@Override
	public final void addGameObject_(GameObject2D go, boolean added) {
		if (go != null) {
            if (go.isGlobal() || !Instance.getGameSettings().usesRenderChunking()) {
                global.addGameObject(go, added);
            }else {
            	tmp = xyToString(go.getTransform().getChunkX(), go.getTransform().getChunkY());
	            if (!scene.containsKey(tmp)) {
	                scene.put(tmp, new RenderChunk2D(go.getTransform().getChunkX(), go.getTransform().getChunkY(), this));
	            }
	            scene.get(tmp).addGameObject(go, added);
            }
        }
	}

	@Override
	public final GameObject2D removeGameObject_(GameObject2D go, boolean delete) {
		if (go != null) {
			if (go.getRenderChunk() != null) {
            	if(!Instance.getGameSettings().usesRenderChunking()) {
            		global.removeGameObject(go, delete);
            	}else {
					tmpc = go.getRenderChunk();
	                go.getRenderChunk().removeGameObject(go, delete);
	                if(tmpc.isEmpty()&&!tmpc.isglobal) {
	                	scene.remove(xyToString(tmpc.getChunkX(), tmpc.getChunkY()));
	                }
            	}
            } else {
            	global.removeGameObject(go, delete);
            	tmp = xyToString(go.getTransform().getChunkX(), go.getTransform().getChunkY());
                scene.get(tmp).removeGameObject(go, delete);
                if(scene.get(tmp).isEmpty()) {
                	scene.remove(tmp);
                }
                if(Logger.isDebugMode()) {
                	System.err.println("RenderChunk2D is null: "+go);
                }
            }
        }
        return go;
	}

	private static final String DELIMITER = ":";
	public static String xyToString(long x, long y) {
		return x+DELIMITER+y;
	}

	@Override
	public int size() {
		//+1 weil global immer da ist
		return scene.size()+1;
	}
}
