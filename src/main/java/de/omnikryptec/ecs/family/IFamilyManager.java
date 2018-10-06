package de.omnikryptec.ecs.family;

import java.util.List;

import de.omnikryptec.ecs.entity.Entity;

public interface IFamilyManager {
	
	List<Entity> getEntitiesFor(Family family);
	IFamilyManager addFilter(Family family);
	IFamilyManager removeFilter(Family family);
	IFamilyManager addFilteredEntity(Entity entity);
}
