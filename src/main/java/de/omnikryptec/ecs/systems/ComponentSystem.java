package de.omnikryptec.ecs.systems;

import java.util.List;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IEntityManager;

public abstract class ComponentSystem {
    
	private Family family;
	protected List<Entity> entities;
	
	protected ComponentSystem(Family required) {
		this.family = required;
	}
	
	public Family getFamily() {
		return family;
	}

	public void addedToEntityManager(IEntityManager entitymgr) {
		entities = entitymgr.getEntitesFor(family);
	}
	
	public void removedFromEntityManager(IEntityManager entitymgr) {
		entities = null;
	}
	
	public abstract void update(IEntityManager entityManager, float deltaTime);
	    
}
