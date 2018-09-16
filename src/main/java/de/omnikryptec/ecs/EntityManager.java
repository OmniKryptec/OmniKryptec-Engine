package de.omnikryptec.ecs;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;

public class EntityManager {

	private BiMap<Long, Entity> entities;
	private Multimap<ComponentSystem, Entity> entitiesPerSystem;
	private List<ComponentSystem> componentSystems;

	private boolean updating = false;

	public EntityManager() {
		entities = HashBiMap.create();
		entitiesPerSystem = ArrayListMultimap.create();
		componentSystems = new ArrayList<>();
	}

	public long getEntityId(Entity e) {
		return entities.inverse().get(e);
	}

	public Entity getEntity(long e) {
		return entities.get(e);
	}

	public void updateSystems(float dt) {
		if (isUpdating()) {
			throw new IllegalStateException("Already updating!");
		}
		updating = true;
		for (ComponentSystem cs : componentSystems) {
			cs.update(this, entitiesPerSystem.get(cs), dt);
		}
		updating = false;
	}

	public boolean isUpdating() {
		return updating;
	}
}
