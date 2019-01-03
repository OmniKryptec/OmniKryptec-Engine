/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.old.main;

import de.omnikryptec.old.gameobject.*;
import de.omnikryptec.old.renderer.d3.RenderChunk3D;
import de.omnikryptec.old.test.saving.DataMap;
import de.omnikryptec.old.util.Instance;
import de.omnikryptec.old.util.logger.LogLevel;
import de.omnikryptec.old.util.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Scene3D extends AbstractScene3D {

    public final HashMap<ChunkCoord3D, RenderChunk3D> scene = new HashMap<>();
    private long cox = Instance.getGameSettings().getChunkRenderOffsetX(),
	    coy = Instance.getGameSettings().getChunkRenderOffsetY(),
	    coz = Instance.getGameSettings().getChunkRenderOffsetZ();
    private final List<Light3D> lights = new ArrayList<>();

    private final RenderChunk3D global = new RenderChunk3D(0, 0, 0, this, true);

    @Override
    public int size() {
	// global ist immer da
	return scene.size() + 1;
    }

    public Scene3D() {
	this("", null);
    }

    public Scene3D(String name, Camera cam) {
	super(name, cam);
    }

    @Override
    public final void addGameObject_(GameObject3D g, boolean added) {
	if (g != null) {
	    if (g instanceof Camera && Logger.isDebugMode()) {
		Logger.log("A Camera should not be added as a GameObject!", LogLevel.WARNING);
		return;
	    }
	    if (g.isGlobal() || !Instance.getGameSettings().usesRenderChunking()) {
		global.addGameObject(g);
	    }
	    // tmp = xyzToString(g.getTransform().getChunkX(), g.getTransform().getChunkY(),
	    // g.getTransform().getChunkZ());
	    ChunkCoord3D tmp = new ChunkCoord3D(g.getTransform().getChunkX(), g.getTransform().getChunkY(),
		    g.getTransform().getChunkZ());
	    if (!scene.containsKey(tmp)) {
		scene.put(tmp, new RenderChunk3D(g.getTransform().getChunkX(), g.getTransform().getChunkY(),
			g.getTransform().getChunkZ(), this));
	    }
	    scene.get(tmp).addGameObject(g);
	    if (added) {
		g.addedOperation();
	    }
	}
    }

    @Override
    public final GameObject3D removeGameObject_(GameObject3D g, boolean delete) {
	if (g != null) {
	    if (g.getRenderChunk() != null) {
		g.getRenderChunk().removeGameObject(g, delete);
	    } else {
		// tmp = xyzToString(g.getTransform().getChunkX(), g.getTransform().getChunkY(),
		// g.getTransform().getChunkZ());
		ChunkCoord3D tmp = new ChunkCoord3D(g.getTransform().getChunkX(), g.getTransform().getChunkY(),
			g.getTransform().getChunkZ());
		scene.get(tmp).removeGameObject(g, delete);
		g.deleteOperation();
		if (scene.get(tmp).isEmpty()) {
		    scene.remove(tmp);
		}
	    }
	}
	return g;
    }

    @Override
    public final void logic() {
	if (Instance.getGameSettings().usesRenderChunking()) {
	    long cx = getCamera().getTransform().getChunkX();
	    long cy = getCamera().getTransform().getChunkY();
	    long cz = getCamera().getTransform().getChunkZ();
	    RenderChunk3D tmpc;
	    for (long x = -cox + cx; x <= cox + cx; x++) {
		for (long y = -coy + cy; y <= coy + cy; y++) {
		    for (long z = -coz + cz; z <= coz + cz; z++) {
			if ((tmpc = scene.get(/* xyzToString(x, y, z) */new ChunkCoord3D(x, y, z))) != null) {
			    tmpc.logic();
			}
		    }
		}
	    }
	}
	global.logic();
	doLogic();
    }

    @Override
    protected final long render() {
	lights.clear();
	lights.addAll(global.getImportantLights());
	long vertcount = 0;
	if (Instance.getGameSettings().usesRenderChunking()) {
	    long cx = getCamera().getTransform().getChunkX();
	    long cy = getCamera().getTransform().getChunkY();
	    long cz = getCamera().getTransform().getChunkZ();
	    RenderChunk3D tmpc;
	    for (long x = -cox + cx; x <= cox + cx; x++) {
		for (long y = -coy + cy; y <= coy + cy; y++) {
		    for (long z = -coz + cz; z <= coz + cz; z++) {
			if ((tmpc = scene.get(/* xyzToString(x, y, z) */new ChunkCoord3D(x, y, z))) != null) {
			    lights.addAll(tmpc.getImportantLights());
			}
		    }
		}
	    }
	    for (long x = -cox + cx; x <= cox + cx; x++) {
		for (long y = -coy + cy; y <= coy + cy; y++) {
		    for (long z = -coz + cz; z <= coz + cz; z++) {
			if ((tmpc = scene.get(/* xyzToString(x, y, z) */new ChunkCoord3D(x, y, z))) != null) {
			    vertcount += tmpc.render(getRenderConfig());
			}
		    }
		}
	    }
	}
	vertcount += global.render(getRenderConfig());
	return vertcount;
    }

    /**
     * override this to do your scene logic
     */
    protected void doLogic() {
    }

    @Override
    public final List<Light3D> getLights() {
	return lights;
    }

    public final List<Entity> getEntities() {
	final List<Entity> entities = new ArrayList<>();
	entities.addAll(global.getEntities());
	for (RenderChunk3D c : scene.values()) {
	    entities.addAll(c.getEntities());
	}
	return entities;
    }

    // private static final String DELIMITER = ":";
    // private static String xyzToString(long x, long y, long z) {
    // return x + DELIMITER + y + DELIMITER + z;
    // }

    @Override
    public DataMap toDataMap(DataMap data) {
	data.put("name", getName());
	data.put("camera", (getCamera() != null ? getCamera().toDataMap(new DataMap("camera")) : null));
	final List<Entity> entities = getEntities();
	Logger.log("SCENE: " + getName() + " ENTITIES: " + Arrays.toString(entities.toArray()));
	final List<String> entityNames = new ArrayList<>();
	entities.stream().forEach((entity) -> {
	    entityNames.add(entity.getName());
	});
	entities.clear();
	data.put("entityNames", entityNames);
	/*
	 * data.put("renderChunk_global", (global != null ? global.toDataMap(new
	 * DataMap("renderChunk_global")) : null)); int i = 0; for(String g :
	 * scene.keySet()) { data.put("renderChunk_" + i, scene.get(g).toDataMap(new
	 * DataMap(g))); i++; }
	 */
	return data;
    }

    public static Scene3D newInstanceFromDataMap(DataMap data) {
	if (data == null) {
	    return null;
	}
	Scene3D scene;
	try {
	    scene = (Scene3D) byName(data.getString("name"));
	} catch (Exception ex) {
	    scene = null;
	    Logger.logErr("Can't cast to Scene (byName returned wrong type)", ex);
	}
	return (scene != null ? scene : new Scene3D()).fromDataMap(data);
    }

    @Override
    public Scene3D fromDataMap(DataMap data) {
	if (data == null) {
	    return this;
	}
	setName(data.getString("name"));
	DataMap dataMap_temp = data.getDataMap("camera");
	if (dataMap_temp != null) {
	    if (getCamera() != null) {
		getCamera().fromDataMap(dataMap_temp);
	    } else {
		Object temp = Camera.newInstanceFromDataMap(dataMap_temp);
		if (temp != null && temp instanceof Camera) {
		    setCamera((Camera) temp);
		}
	    }
	} else {
	    Logger.log("Camera is null!");
	    setCamera(null);
	}
	final List<Entity> entities = getEntities();
	final List<String> entityNames = data.getList("entityNames", String.class);
	if (entityNames == null) {
	    Logger.log("Entity name list is null!", LogLevel.WARNING);
	    return this;
	}
	for (String entitiyName : entityNames) {
	    try {
		final Entity entity = GameObject.byName(Entity.class, entitiyName, false);
		if (entity != null) {
		    for (Entity e : entities) {
			if (e.getName().equals(entity.getName())) {
			    e.setValuesFrom(entity);
			    break;
			}
		    }
		    Logger.log("Added: " + entity);
		    addGameObject(entity);
		} else {
		    Logger.log("Entity not found!", LogLevel.WARNING);
		}
	    } catch (Exception ex) {
		Logger.logErr("Error while adding entity: " + ex, ex);
	    }
	}
	/*
	 * dataMap_temp = data.getDataMap("renderChunk_global"); if(dataMap_temp !=
	 * null) { if(global != null) { global.fromDataMap(dataMap_temp); } else {
	 * Object temp = RenderChunk.newInstanceFromDataMap(dataMap_temp); if(temp !=
	 * null && temp instanceof RenderChunk) { global = (RenderChunk) temp; } } }
	 * else { Logger.log("Global RenderChunk is null!"); global = null; } int i = 0;
	 * while((dataMap_temp = data.getDataMap("renderChunk_" + i)) != null) { String
	 * name_temp = dataMap_temp.getString("name"); RenderChunk chunk =
	 * scene.get(name_temp); if(chunk != null) { chunk.fromDataMap(dataMap_temp); }
	 * else { Object temp = RenderChunk.newInstanceFromDataMap(dataMap_temp);
	 * if(temp != null && temp instanceof RenderChunk) { scene.put(name_temp,
	 * (RenderChunk) temp); } } i++; }
	 */
	return this;
    }

    public Scene3D setValuesFrom(Scene3D scene) {
	if (scene == null) {
	    return this;
	}
	setName(scene.getName());
	return this;
    }

}
