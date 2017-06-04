package omnikryptec.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import omnikryptec.entity.Camera;
import omnikryptec.entity.GameObject;
import omnikryptec.entity.Light;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.physics.PhysicsWorld;
import omnikryptec.renderer.IRenderer;
import omnikryptec.renderer.RenderChunk;
import omnikryptec.renderer.RenderChunk.AllowedRenderer;
import omnikryptec.shader_files.LightShader;
import omnikryptec.util.PhysicsUtil;

public class Scene {

	private final Map<String, RenderChunk> scene = new HashMap<>();
	private Camera cam;
	private long cox = OmniKryptecEngine.getInstance().getDisplayManager().getSettings().getChunkOffsetX(),
			coy = OmniKryptecEngine.getInstance().getDisplayManager().getSettings().getChunkOffsetY(),
			coz = OmniKryptecEngine.getInstance().getDisplayManager().getSettings().getChunkOffsetZ();
	private float[] clearcolor = { 0, 0, 0, 0 };
	private PhysicsWorld physicsWorld = null;
	private final Map<LightShader, List<Light>> rel_lights = new HashMap<>();
    private final Map<LightShader, List<Light>> global_lights = new HashMap<>();
	/* Temp Variables */
	private String tmp;
	private long cx, cy, cz;
	private RenderChunk tmpc;
	
	
	public Scene(Camera cam) {
		this.cam = cam;
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

	public final Scene frame(float maxexpenlvl, float minexplvl,  AllowedRenderer info, IRenderer... re) {
		rel_lights.clear();
		rel_lights.putAll(global_lights);
		if (isUsingPhysics()) {
			physicsWorld.stepSimulation();
		}
		cx = cam.getChunkX();
		cy = cam.getChunkY();
		cz = cam.getChunkZ();
		for (long x = -cox + cx; x <= cox + cx; x++) {
			for (long y = -coy + cy; y <= coy + cy; y++) {
				for (long z = -coz + cz; z <= coz + cz; z++) {
					if ((tmpc = scene.get(xyzToString(x, y, z))) != null) {
						tmpc.frame(maxexpenlvl, minexplvl, info, re);
						rel_lights.putAll(tmpc.getLights());
					}
				}
			}
		}
		cam.doLogic0();
		doLogic();
		return this;
	}

	protected void doLogic() {
	}

	public final List<Light> getRenderLights(LightShader usingShader) {
		return rel_lights.get(usingShader);
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

	private static String xyzToString(long x, long y, long z) {
		return x + ":" + y + ":" + z;
	}

}
