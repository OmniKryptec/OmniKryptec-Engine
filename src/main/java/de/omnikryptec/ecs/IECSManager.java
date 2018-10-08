package de.omnikryptec.ecs;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import de.omnikryptec.core.Updateable;
import de.omnikryptec.ecs.impl.ECSManager;
import de.omnikryptec.ecs.system.ComponentSystem;

//TODO is updateable useful here?
public interface IECSManager extends Updateable{

	//TODO useful or too much?
	public static IECSManager createDefault() {
		return new ECSManager();
	}
	
	void addEntity(Entity entity);
	void removeEntity(Entity entity);
	
	void onEntityComponentsChanged(Entity entity);
	
	void addSystem(ComponentSystem system);
	void removeSystem(ComponentSystem system);
	
	List<Entity> getEntitesFor(BitSet f);
	
	Collection<Entity> getAll();
}
