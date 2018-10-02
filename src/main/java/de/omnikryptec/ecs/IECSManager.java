package de.omnikryptec.ecs;

import de.omnikryptec.ecs.systems.ComponentSystem;

public interface IECSManager {

	void addEntity(Entity entity);
	void removeEntity(Entity entity);
	
	void addSystem(ComponentSystem componentSystem);
	void removeSystem(ComponentSystem componentSystem);
	
	void update(float deltaTIme);
}
