package de.omnikryptec.ecs.system;

import java.util.Collection;

public interface ISystemManager {

	ISystemManager addSystem(ComponentSystem system);
	ISystemManager removeSystem(ComponentSystem system);
	
	Collection<ComponentSystem> getAll();
	
}
