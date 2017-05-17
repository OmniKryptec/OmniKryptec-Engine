package omnikryptec.main;

import java.util.HashMap;
import java.util.Map;

import omnikryptec.renderer.RenderChunk;
import omnikryptec.storing.GameObject;

public class Scene {

	private Map<String, RenderChunk> scene = new HashMap<>();

	private String tmp;

	public void addGameObject(GameObject g) {
		tmp = xyzToString(g.getChunkX(), g.getChunkY(), g.getChunkZ());
		if (!scene.containsKey(tmp)) {
			scene.put(tmp, new RenderChunk(g.getChunkX(), g.getChunkY(), g.getChunkZ()));
		}
		scene.get(tmp).addGameObject(g);
	}

	public GameObject removeGameObject(GameObject g) {
		if (g != null) {
			if (g.getMyChunk() != null) {
				g.getMyChunk().removeGameObject(g);
			} else {
				tmp = xyzToString(g.getChunkX(), g.getChunkY(), g.getChunkZ());
				scene.get(tmp).removeGameObject(g);
			}
		}
		return g;
	}

	private static String xyzToString(long x, long y, long z) {
		return x + ":" + y + ":" + z;
	}

}
