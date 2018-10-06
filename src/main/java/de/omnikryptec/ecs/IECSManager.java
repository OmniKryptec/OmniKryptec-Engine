package de.omnikryptec.ecs;

import java.util.Collection;
import java.util.List;

import de.omnikryptec.ecs.entity.Entity;
import de.omnikryptec.ecs.family.Family;
import de.omnikryptec.ecs.system.ComponentSystem;

public interface IECSManager {

	void addEntity(Entity entity);
	void removeEntity(Entity entity);
	
	void addSystem(ComponentSystem system);
	void removeSystem(ComponentSystem system);
	
	List<Entity> getEntitesFor(Family f);
	
	void update(float deltaTime);
	Collection<Entity> getAll();
}
