package de.omnikryptec.ecs;

import java.util.Collection;

import de.omnikryptec.ecs.systems.ComponentSystem;

public interface IEntityManager {

	void addEntity(Entity entity);
	void removeEntity(Entity entity);
	
	void addSystem(ComponentSystem system);
	void removeSystem(ComponentSystem system);
	
	void update(float deltaTime);
	Collection<Entity> getAll();
}
