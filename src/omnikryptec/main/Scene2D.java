package omnikryptec.main;

import java.util.HashMap;

import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.GameObject2D;
import omnikryptec.renderer.d2.DefaultRenderer2D;
import omnikryptec.renderer.d2.RenderChunk2D;
import omnikryptec.renderer.d2.Renderer2D;
import omnikryptec.renderer.d3.RenderChunk3D;
import omnikryptec.renderer.d3.RenderConfiguration;
import omnikryptec.util.Instance;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public class Scene2D extends AbstractScene2D{
	
	private HashMap<String, RenderChunk2D> scene = new HashMap<>();
	private RenderChunk2D global = new RenderChunk2D(0, 0, this, true);
	private Renderer2D renderer;

	private long cx,cy,cox,coy;
	private RenderChunk2D tmpc;
	private String tmp;
	
	public Scene2D() {
		this("", null);
	}
	
	public Scene2D(String name, Camera cam) {
		super(name, cam);
		renderer = new DefaultRenderer2D(this);
	}
	

	@Override
	protected void logic() {
		if(Instance.getGameSettings().usesRenderChunking()) {
		    cx = getCamera().getTransform().getChunkX();
		    cy = getCamera().getTransform().getChunkY();
		    for (long x = -cox + cx; x <= cox + cx; x++) {
		        for (long y = -coy + cy; y <= coy + cy; y++) {
	                if ((tmpc = scene.get(xyToString(x, y))) != null) {
	                    tmpc.logic();
	                }
		        }
		    }
		}
        global.logic();
        doLogic();
	}

	protected void doLogic() {}

	@Override
	protected long render() {
		return renderer.render(global, getCamera().getTransform().getChunkX(), getCamera().getTransform().getChunkY(), cox, coy, scene);
	}

	@Override
	public void addGameObject(GameObject2D go) {
		if (go != null) {
            if (go.isGlobal() || !Instance.getGameSettings().usesRenderChunking()) {
                global.addGameObject(go);
            }
            tmp = xyToString(go.getTransform().getChunkX(), go.getTransform().getChunkY());
            if (!scene.containsKey(tmp)) {
                scene.put(tmp, new RenderChunk2D(go.getTransform().getChunkX(), go.getTransform().getChunkY(), this));
            }
            scene.get(tmp).addGameObject(go);
        }
	}

	@Override
	public GameObject2D removeGameObject(GameObject2D go, boolean delete) {
		if (go != null) {
            if (go.getRenderChunk() != null) {
                go.getRenderChunk().removeGameObject(go, delete);
            } else {
                tmp = xyToString(go.getTransform().getChunkX(), go.getTransform().getChunkY());
                scene.get(tmp).removeGameObject(go, delete);
                go.deleteOperation();
            }
        }
        return go;
	}

	
	public static String xyToString(long x, long y) {
		return x+":"+y;
	}
}
