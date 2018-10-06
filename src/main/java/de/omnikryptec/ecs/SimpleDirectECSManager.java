package de.omnikryptec.ecs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.omnikryptec.ecs.entity.Entity;
import de.omnikryptec.ecs.family.Family;
import de.omnikryptec.ecs.system.ComponentSystem;
import de.omnikryptec.util.data.CountingMap;

public class SimpleDirectECSManager implements IECSManager {

	//Families
	private CountingMap<Family> systemsPerFamilies;
	private ListMultimap<Family, Entity> entitiesPerFamily;
	private ListMultimap<Entity, Family> familiesPerEntity;
	//Systems
	private List<ComponentSystem> systems;
	//Entities
	private Collection<Entity> all;
	private Collection<Entity> unmodifiableAll;
	
	public SimpleDirectECSManager() {
		this.entitiesPerFamily = ArrayListMultimap.create();
		this.familiesPerEntity = ArrayListMultimap.create();
		this.systems = new ArrayList<>();
		this.all = new ArrayList<>();
		this.unmodifiableAll = Collections.unmodifiableCollection(all);
		this.systemsPerFamilies = new CountingMap<>();
	}

	@Override
	public void addEntity(Entity entity) {
		for (ComponentSystem system : systems) {
			if (entity.getFamily().contains(system.getFamily())) {
				entitiesPerFamily.put(system.getFamily(), entity);
				familiesPerEntity.put(entity, system.getFamily());
			}
		}
		all.add(entity);
	}

	@Override
	public void removeEntity(Entity entity) {
		all.remove(entity);
		for (Family f : familiesPerEntity.get(entity)) {
				entitiesPerFamily.remove(f, entity);
		}
		familiesPerEntity.removeAll(entity);
	}

	@Override
	public void addSystem(ComponentSystem componentSystem) {
		systemsPerFamilies.increment(componentSystem.getFamily());
		if (!all.isEmpty()) {
			if (!entitiesPerFamily.containsKey(componentSystem.getFamily())) {
				for (Entity e : all) {
					if (e.getFamily().contains(componentSystem.getFamily())) {
						entitiesPerFamily.put(componentSystem.getFamily(), e);
						familiesPerEntity.put(e, componentSystem.getFamily());
					}
				}
			}
		}
		systems.add(componentSystem);
		componentSystem.addedToEntityManager(this);
	}

	@Override
	public void removeSystem(ComponentSystem componentSystem) {
		componentSystem.removedFromEntityManager(this);
		systems.remove(componentSystem);
		if (systemsPerFamilies.decrement(componentSystem.getFamily()) == 0) {
			entitiesPerFamily.removeAll(componentSystem.getFamily());
			for(Entity e : all) {
				familiesPerEntity.remove(e, componentSystem.getFamily());
			}
		}
	}

	@Override
	public void update(float deltaTime) {
		for (ComponentSystem system : systems) {
			system.update(this, deltaTime);
		}
	}

	@Override
	public Collection<Entity> getAll() {
		return unmodifiableAll;
	}

	@Override
	public List<Entity> getEntitesFor(Family f) {
		return Collections.unmodifiableList(entitiesPerFamily.get(f));
	}

}
