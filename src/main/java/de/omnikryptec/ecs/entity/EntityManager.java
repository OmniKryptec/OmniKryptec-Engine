package de.omnikryptec.ecs.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class EntityManager implements IEntityManager{

	private Collection<Entity> entities;
	private Collection<Entity> unmodifiableEntities;
	
	public EntityManager() {
		//TODO Set or List?!
		this.entities = new ArrayList<>();
		this.unmodifiableEntities = Collections.unmodifiableCollection(this.entities);
	}
	
	@Override
	public EntityManager addEntity(Entity entity) {
		entities.add(entity);
		return this;
	}

	@Override
	public EntityManager removeEntity(Entity entity) {
		entities.remove(entity);
		return this;
	}

	@Override
	public Collection<Entity> getAll() {
		return unmodifiableEntities;
	}

}
