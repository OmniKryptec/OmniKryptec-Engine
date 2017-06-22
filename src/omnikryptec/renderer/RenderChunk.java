package omnikryptec.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import omnikryptec.entity.Entity;
import omnikryptec.entity.GameObject;
import omnikryptec.light.Light;
import omnikryptec.light.DeferredLightPrepare;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene;
import omnikryptec.model.AdvancedModel;
import omnikryptec.model.Material;

public class RenderChunk {

	private static int WIDTH = OmniKryptecEngine.instance().getDisplayManager().getSettings().getChunkWidth();
	private static int HEIGHT = OmniKryptecEngine.instance().getDisplayManager().getSettings().getChunkHeight();
	private static int DEPTH = OmniKryptecEngine.instance().getDisplayManager().getSettings().getChunkDepth();

	/**
	 * changing at runtime may cause errors
	 * 
	 * @param width
	 * @param height
	 * @param depth
	 */
	public static void setSize(int width, int height, int depth) {
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

	public static final void cleanup() {

	}

	private final long x, y, z;
	private final Scene scene;

	public RenderChunk(long x, long y, long z, Scene scene) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.scene = scene;
	}

	private final RenderMap<Renderer, RenderMap<AdvancedModel, List<Entity>>> chunk = new RenderMap<>(Renderer.class);
	private final ArrayList<GameObject> other = new ArrayList<>();
	private final Map<DeferredLightPrepare, List<Light>> lights = new HashMap<>();

	private Entity tmp;
	private Renderer tmpr;
	private RenderMap<AdvancedModel, List<Entity>> map;
	private List<Entity> list;
	private Material m;
	private AdvancedModel am;

	private Light tmpl;
	private DeferredLightPrepare lightpre;
	private List<Light> lightl;

	public void addGameObject(GameObject g) {
		if (g != null) {
			if (g instanceof Entity) {
				tmp = (Entity) g;
				am = null;
				m = null;
				if ((am = tmp.getAdvancedModel()) != null) {
					if ((m = am.getMaterial()) != null) {
						if ((tmpr = m.getRenderer()) != null) {
							map = chunk.get(tmpr);
							if (map == null) {
								map = new RenderMap<>(AdvancedModel.class);
								chunk.put(tmpr, map);
							}
							list = map.get(am);
							if (list == null) {
								list = new ArrayList<>();
								map.put(am, list);
							}
							list.add(tmp);
						} else if (Logger.isDebugMode()) {
							Logger.log("IRenderer is null", LogLevel.WARNING);
						}
					} else if (Logger.isDebugMode()) {
						Logger.log("Material is null", LogLevel.WARNING);
					}
				} else if (Logger.isDebugMode()) {
					Logger.log("TexturedModel is null", LogLevel.WARNING);
				}
			} else if (g instanceof Light) {
				tmpl = (Light) g;
				if ((lightpre = tmpl.getShader()) != null) {
					if ((lightl = lights.get(lightpre)) == null) {
						lights.put(lightpre, new ArrayList<>());
					}
					lights.get(lightpre).add(tmpl);
				}
			} else {
				other.add(g);
			}
			g.setMyChunk(this);
		}
	}

	public GameObject removeGameObject(GameObject g, boolean delete) {
		if (g != null) {
			if (g instanceof Entity) {
				tmp = (Entity) g;
				am = null;
				m = null;
				if ((am = tmp.getAdvancedModel()) != null) {
					if ((m = am.getMaterial()) != null) {
						if ((tmpr = m.getRenderer()) != null) {
							map = chunk.get(tmpr);
							if (map != null) {
								list = map.get(am);
								if (list != null) {
									list.remove(tmp);
									if (list.isEmpty()) {
										map.remove(am);
									}
									if (map.isEmpty()) {
										chunk.remove(tmpr);
									}
									if (delete) {
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
					} else if (Logger.isDebugMode()) {
						Logger.log("Material is null", LogLevel.WARNING);
					}
				} else if (Logger.isDebugMode()) {
					Logger.log("TexturedModel is null", LogLevel.WARNING);
				}
			} else if (g instanceof Light) {
				tmpl = (Light) g;
				if ((lightpre = tmpl.getShader()) != null) {
					if ((lightl = lights.get(lightpre)) != null) {
						lightl.remove(tmpl);
						if (lightl.isEmpty()) {
							lights.remove(lightpre);
						}
						if (delete) {
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
		All, EvElse, OnlThis;
	}

	private final Renderer[] empty_array = new Renderer[] { null };
	private Renderer r;
	private GameObject g;

	public void frame(float maxExpenLvl, float minexplvl, AllowedRenderer type, Renderer... rend) {
		if (rend == null || rend.length == 0) {
			rend = empty_array;
		}
		for (Renderer keysArray : chunk.keysArray()) {
			r = keysArray;
			if (r != null && r.expensiveLevel() <= maxExpenLvl && r.expensiveLevel() >= minexplvl
					&& (type == AllowedRenderer.All || (type == AllowedRenderer.OnlThis && contains(rend, r))
							|| (type == AllowedRenderer.EvElse && !contains(rend, r)))) {
				r.render(scene, chunk.get(r));
			}
		}
		for (int i = 0; i < other.size(); i++) {
			g = other.get(i);
			if (g != null && g.isActive()) {
				g.doLogic0();
			}
		}
	}

	private boolean contains(Object[] array, Object obj) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == obj) {
				return true;
			}
		}
		return false;
	}

	public Scene getScene() {
		return scene;
	}

	public Map<DeferredLightPrepare, List<Light>> getLights() {
		return lights;
	}

}
