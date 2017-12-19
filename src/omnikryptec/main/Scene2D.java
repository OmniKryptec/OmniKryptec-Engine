package omnikryptec.main;

import java.util.HashMap;

import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.GameObject2D;
import omnikryptec.renderer.d2.DefaultRenderer2D;
import omnikryptec.renderer.d2.RenderChunk2D;
import omnikryptec.renderer.d2.Renderer2D;
import omnikryptec.settings.GameSettings;
import omnikryptec.util.Instance;

public class Scene2D extends AbstractScene2D{
	
	private HashMap<String, RenderChunk2D> scene = new HashMap<>();
	private RenderChunk2D global = new RenderChunk2D(0, 0, this, true);
	private Renderer2D renderer;

	private long cx,cy;
	private int cox,coy;
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
		cox = OmniKryptecEngine.instance().getGameSettings().getInteger(GameSettings.CHUNK_OFFSET_2D_X);
		coy = OmniKryptecEngine.instance().getGameSettings().getInteger(GameSettings.CHUNK_OFFSET_2D_Y);
	}
	
	public void setRenderer(Renderer2D renderer) {
		this.renderer = renderer;
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

	private static final String DELIMITER = ":";
	public static String xyToString(long x, long y) {
		return x+DELIMITER+y;
	}
}
