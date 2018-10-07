package de.omnikryptec.ecs.entity;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;

public interface IEntityManager {

	IEntityManager addEntity(Entity entity);
	IEntityManager removeEntity(Entity entity);
	
	List<Entity> getEntitiesFor(BitSet family);
	IEntityManager addFilter(BitSet family);
	IEntityManager removeFilter(BitSet family);
	
	Collection<Entity> getAll();
	
}
