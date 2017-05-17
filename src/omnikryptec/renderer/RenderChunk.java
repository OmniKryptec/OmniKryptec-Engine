package omnikryptec.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import omnikryptec.logger.Logger;
import omnikryptec.logger.Logger.LogLevel;
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

	private long x, y, z;

	public RenderChunk(long x, long y, long z) {
		this.x = x;
		this.y = y;
		this.z = z;
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
					if (!chunk.containsKey(tmpr)) {
						chunk.put(tmpr, new HashMap<>());
					}
					if (!chunk.get(tmpr).containsKey(tmp.getTexturedModel())) {
						chunk.get(tmpr).put(tmp.getTexturedModel(), new ArrayList<>());
					}
					chunk.get(tmpr).get(tmp.getTexturedModel()).add(tmp);
				} else if (Logger.isInDebugMode()) {
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
				} else if (Logger.isInDebugMode()) {
					Logger.log("IRenderer is null", LogLevel.WARNING);
				}
			} else {
				other.remove(g);
			}
			g.setMyChunk(null);
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
}
