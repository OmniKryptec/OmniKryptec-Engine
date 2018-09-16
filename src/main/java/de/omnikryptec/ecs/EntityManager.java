package de.omnikryptec.ecs;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;

public class EntityManager {

	private BiMap<Long, Entity> entities;
	private Multimap<ComponentSystem, Entity> entitiesPerSystem;
	//private Multimap<Integer, ComponentSystem> systemsPerComponentType;
	//private Multimap<ComponentSystem, Integer> componentTypesPerSystem;
	private Set<ComponentSystem> componentSystems;

	private boolean updating = false;

	public EntityManager() {
		entities = HashBiMap.create();
		entitiesPerSystem = ArrayListMultimap.create();
		//systemsPerComponentType = ArrayListMultimap.create();
		componentSystems = new HashSet<>();
	}

	public long getEntityId(Entity e) {
		return entities.inverse().get(e);
	}

	public Entity getEntity(long e) {
		return entities.get(e);
	}
	
	public void addEntity(Entity e) {
		for(ComponentSystem sys : componentSystems) {
			if(e.getComponents().keySet().containsAll(sys.usesComponents())) {
				entitiesPerSystem.get(sys).add(e);
			}
		}
	}

	public void addSystem(ComponentSystem sys) {
		componentSystems.add(sys);
//		for(Integer i : sys.usesComponents()) {
//			systemsPerComponentType.put(i, sys);
//		}
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
