package de.omnikryptec.ecs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.omnikryptec.ecs.systems.ComponentSystem;
import de.omnikryptec.util.data.CountingMap;

public class EntityManager {

	private CountingMap<Family> systemsPerFamilies;
	private ListMultimap<Family, Entity> entitiesForSystems;
	private List<ComponentSystem> systems;
	private Collection<Entity> all;

	public EntityManager() {
		this.entitiesForSystems = ArrayListMultimap.create();
		this.systems = new ArrayList<>();
		this.all = new ArrayList<>();
		systemsPerFamilies = new CountingMap<>();
	}

	public void addEntity(Entity entity) {
		for (ComponentSystem system : systems) {
			if (entity.getFamily().contains(system.getRequiredComponents())) {
				entitiesForSystems.put(system.getRequiredComponents(), entity);
			}
		}
		all.add(entity);
	}

	public void removeEntity(Entity entity) {
		for (Family f : entitiesForSystems.keySet()) {
			if (entity.getFamily().contains(f)) {
				entitiesForSystems.remove(f, entity);
			}
		}
		all.remove(entity);
	}

	public void addSystem(ComponentSystem componentSystem, boolean checkExistingEntities) {
		systems.add(componentSystem);
		systemsPerFamilies.increment(componentSystem.getRequiredComponents());
		if (checkExistingEntities && !all.isEmpty()) {
			if (!entitiesForSystems.containsKey(componentSystem.getRequiredComponents())) {
				for (Entity e : all) {
					if (e.getFamily().contains(componentSystem.getRequiredComponents())) {
						entitiesForSystems.put(componentSystem.getRequiredComponents(), e);
					}
				}
			}
		}
	}

	public void removeSystem(ComponentSystem componentSystem) {
		systems.remove(componentSystem);
		if (systemsPerFamilies.decrement(componentSystem.getRequiredComponents()) == 0) {
			systemsPerFamilies.remove(componentSystem.getRequiredComponents());
			entitiesForSystems.removeAll(componentSystem.getRequiredComponents());
		}
	}

	public void update(float deltaTime) {
		for (ComponentSystem system : systems) {
			system.update(this, entitiesForSystems.get(system.getRequiredComponents()), deltaTime);
		}
	}

	public Collection<Entity> getAll() {
		return all;
	}

}
