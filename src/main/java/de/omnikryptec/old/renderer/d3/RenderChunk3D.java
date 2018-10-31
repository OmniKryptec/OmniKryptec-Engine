/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.renderer.d3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;

import de.omnikryptec.old.gameobject.Entity;
import de.omnikryptec.old.gameobject.GameObject;
import de.omnikryptec.old.gameobject.GameObject3D;
import de.omnikryptec.old.gameobject.Light3D;
import de.omnikryptec.old.main.AbstractScene3D;
import de.omnikryptec.old.main.GameObjectContainer;
import de.omnikryptec.old.main.OmniKryptecEngine;
import de.omnikryptec.old.resource.model.AdvancedModel;
import de.omnikryptec.old.resource.model.Material;
import de.omnikryptec.old.test.saving.DataMap;
import de.omnikryptec.old.test.saving.DataMapSerializable;
import de.omnikryptec.old.util.KeyArrayHashMap;
import de.omnikryptec.old.util.SerializationUtil;
import de.omnikryptec.old.util.logger.LogLevel;
import de.omnikryptec.old.util.logger.Logger;

public class RenderChunk3D implements DataMapSerializable, GameObjectContainer<GameObject3D> {

    public static final int DEFAULT_CAPACITY = 50;

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
    @Deprecated
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

    private long x, y, z;
    private final AbstractScene3D scene;

    private RenderChunk3D() {
	this(0, 0, 0, null);
    }

    public RenderChunk3D(long x, long y, long z, AbstractScene3D scene) {
	this(x, y, z, scene, false);
    }

    public RenderChunk3D(long x, long y, long z, AbstractScene3D scene, boolean global) {
	this.x = x;
	this.y = y;
	this.z = z;
	this.scene = scene;
	this.isglobal = global;
    }

    private final KeyArrayHashMap<Renderer, KeyArrayHashMap<AdvancedModel, List<Entity>>> chunk = new KeyArrayHashMap<>(
	    Renderer.class);
    private final List<Renderer> prios = new ArrayList<>();
    private final ArrayList<GameObject> other = new ArrayList<>();
    private final List<Light3D> lights = new ArrayList<>();

    public final boolean isglobal;

    private static final Comparator<Renderer> priority_sorter = new Comparator<Renderer>() {

	@Override
	public int compare(Renderer o1, Renderer o2) {
	    if (o1.priority() > o2.priority()) {
		return 1;
	    } else if (o1.priority() < o2.priority()) {
		return -1;
	    } else {
		return 0;
	    }
	}
    };

    private Entity tmp;
    private Renderer tmpr;
    private KeyArrayHashMap<AdvancedModel, List<Entity>> map;
    private List<Entity> list;
    private Material m;
    private AdvancedModel am;

    @Override
    public void addGameObject(GameObject3D g, boolean added) {
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
				map = new KeyArrayHashMap<>(AdvancedModel.class);
				chunk.put(tmpr, map);
				prios.add(tmpr);
				prios.sort(priority_sorter);
			    }
			    list = map.get(am);
			    if (list == null) {
				list = new ArrayList<>(DEFAULT_CAPACITY);
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
	    } else if (g instanceof Light3D) {
		lights.add((Light3D) g);
	    } else {
		other.add(g);
	    }
	    g.setRenderChunk3D(this);
	    if (added) {
		g.addedOperation();
	    }
	}
    }

    @Override
    public GameObject3D removeGameObject(GameObject3D g, boolean delete) {
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
					prios.remove(tmpr);
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
	    } else if (g instanceof Light3D) {
		lights.remove(g);
	    } else {
		other.remove(g);
	    }
	    if (delete) {
		g.deleteOperation();
	    }
	    g.setRenderChunk3D(null);
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

    private KeyArrayHashMap<AdvancedModel, List<Entity>> tmpmap;
    private List<Entity> tmplist;

    public void logic() {
	for (int i = 0; i < chunk.keysArray().length; i++) {
	    tmpmap = chunk.get(chunk.keysArray()[i]);
	    for (int j = 0; j < tmpmap.keysArray().length; j++) {
		tmplist = tmpmap.get(tmpmap.keysArray()[j]);
		for (int k = 0; k < tmplist.size(); k++) {
		    tmplist.get(k).doLogic().checkChunkPos(false);
		}
	    }
	}
	for (int i = 0; i < other.size(); i++) {
	    other.get(i).doLogic().checkChunkPos(false);
	}
	for (int i = 0; i < lights.size(); i++) {
	    lights.get(i).doLogic().checkChunkPos(false);
	}
    }

    private LinkedList<Renderer> renderlist;
    private long vertcount = 0;
    private KeyArrayHashMap<AdvancedModel, List<Entity>> rendermap;

    public long render(RenderConfiguration config) {
	vertcount = 0;
	renderlist = config.getRenderer();
	for (Renderer renderer : renderlist) {
	    rendermap = chunk.get(renderer);
	    if (rendermap != null) {
		vertcount += renderer.render(scene, rendermap, config);
	    }
	}
	return vertcount;
    }

    public AbstractScene3D getScene() {
	return scene;
    }

    // TOD- seperate all lights and important lights!
    private final List<Light3D> lightstoreturn = new ArrayList<>();

    public List<Light3D> getImportantLights() {
	return lights;
    }

    @Override
    public int size() {
	return chunk.size() + other.size() + lights.size();
    }

    public List<Entity> getEntities() {
	final ArrayList<Entity> entities = new ArrayList<>();
	other.stream().filter((gameObject) -> gameObject instanceof Entity).forEach((entity) -> {
	    entities.add((Entity) entity);
	});
	for (Renderer renderer : chunk.keysArray()) {
	    KeyArrayHashMap<AdvancedModel, List<Entity>> temp = chunk.get(renderer);
	    for (AdvancedModel advancedModel : temp.keysArray()) {
		entities.addAll(temp.get(advancedModel));
	    }
	}
	return entities;
    }

    @Override
    public DataMap toDataMap(DataMap data) {
	data.put("name", getName());
	final HashMap<Class<?>, ArrayList<Entity>> classesEntities = new HashMap<>();
	final HashMap<String, ArrayList<String>> chunk_renderer_entities = new HashMap<>();
	for (Renderer renderer : chunk.keysArray()) {
	    KeyArrayHashMap<AdvancedModel, List<Entity>> rendererModels = chunk.get(renderer);
	    for (AdvancedModel advancedModel : rendererModels.keysArray()) {
		final List<Entity> entities = rendererModels.get(advancedModel);
		ArrayList<Entity> listTemp = classesEntities.get(renderer.getClass());
		if (listTemp == null) {
		    listTemp = new ArrayList<>();
		    classesEntities.put(renderer.getClass(), listTemp);
		}
		for (Entity entity : entities) {
		    listTemp.add(entity);
		}
	    }
	}
	classesEntities.keySet().stream().forEach((c) -> {
	    final ArrayList<String> names = new ArrayList<>();
	    classesEntities.get(c).stream().forEach((entity) -> {
		names.add(entity.getName());
	    });
	    chunk_renderer_entities.put(c.getName(), names);
	});
	classesEntities.clear();
	data.put("chunk_renderer_entities", chunk_renderer_entities);
	final HashMap<Class<?>, ArrayList<GameObject>> classesOthers = SerializationUtil
		.gameObjectsToClassesGameObjects(other);
	final HashMap<Class<?>, ArrayList<String>> other_gameObjects = new HashMap<>();
	classesOthers.keySet().stream().forEach((c) -> {
	    final ArrayList<String> names = new ArrayList<>();
	    classesOthers.get(c).stream().forEach((gameObject) -> {
		names.add(gameObject.getName());
	    });
	    other_gameObjects.put(c, names);
	});
	classesOthers.clear();
	data.put("other_classes_gameObjects", other_gameObjects);
	return data;
    }

    public static RenderChunk3D newInstanceFromDataMap(DataMap data) {
	if (data == null) {
	    return null;
	}
	return new RenderChunk3D().fromDataMap(data);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public RenderChunk3D fromDataMap(DataMap data) {
	if (data == null) {
	    return this;
	}
	Vector3f temp_v = SerializationUtil.stringToVector3f(data.getString("name"));
	if (temp_v != null) {
	    x = (long) temp_v.x;
	    y = (long) temp_v.y;
	    z = (long) temp_v.z;
	}
	other.clear();
	final Map<Class, Object> other_classes_gameObjects = data.getMap("other_classes_gameObjects", Class.class,
		Object.class);
	if (other_classes_gameObjects != null) {
	    other_classes_gameObjects.keySet().stream().forEach((c) -> {
		final Object object = other_classes_gameObjects.get(c);
		if (object != null && object instanceof List) {
		    ((List) object).stream().forEach((name) -> {
			try {
			    Object gameObject = c.getDeclaredMethod("byName", Class.class, String.class).invoke(c,
				    "" + name);
			    if (gameObject != null && gameObject.getClass() == c) {
				other.add((GameObject) gameObject);
			    }
			} catch (Exception ex) {
			    Logger.logErr("Error while loading GameObject to RenderChunk: " + ex, ex);
			}
		    });
		}
	    });
	    other_classes_gameObjects.clear();
	}
	for (Renderer renderer : chunk.keysArray()) {
	    chunk.remove(renderer);
	}
	final Map<String, Object> chunk_renderer_entities = data.getMap("chunk_renderer_entities", String.class,
		Object.class);
	if (chunk_renderer_entities != null) {
	    chunk_renderer_entities.keySet().stream().forEach((c) -> {
		final KeyArrayHashMap<AdvancedModel, List<Entity>> rendererModels = new KeyArrayHashMap<>(
			AdvancedModel.class);
		final Object object = chunk_renderer_entities.get(c);
		if (object != null && object instanceof List) {
		    ((List) object).stream().forEach((name) -> {
			try {
			    Entity entity = GameObject.byName(Entity.class, "" + name, false);
			    if (entity != null) {
				List<Entity> entities = rendererModels.get(entity.getAdvancedModel());
				if (entities == null) {
				    entities = new ArrayList<>();
				    rendererModels.put(entity.getAdvancedModel(), entities);
				}
				entities.add(entity);
			    }
			} catch (Exception ex) {
			    Logger.logErr("Error while loading Entity to RenderChunk: " + ex, ex);
			}
		    });
		}
		if (!rendererModels.isEmpty()) {
		    chunk.put(RendererRegistration.byClass(SerializationUtil.classForName(c)), rendererModels);
		}
	    });
	    chunk_renderer_entities.clear();
	}
	return this;
    }

    @Override
    public String getName() {
	return SerializationUtil.vector3fToString(new Vector3f(x, y, z));
    }

}
