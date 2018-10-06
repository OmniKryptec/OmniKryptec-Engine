package de.omnikryptec.ecs.entity;

import java.util.Collection;

public interface IEntityManager {

	IEntityManager addEntity(Entity entity);
	IEntityManager removeEntity(Entity entity);
	
	Collection<Entity> getAll();
	
}
