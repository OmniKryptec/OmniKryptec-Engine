package de.omnikryptec.ecs.system;

import java.util.List;

import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.entity.Entity;
import de.omnikryptec.ecs.family.Family;

public abstract class ComponentSystem {
    
	private Family family;
	protected List<Entity> entities;
	
	protected ComponentSystem(Family required) {
		this.family = required;
	}
	
	public Family getFamily() {
		return family;
	}

	public void addedToEntityManager(IECSManager entitymgr) {
		entities = entitymgr.getEntitesFor(family);
	}
	
	public void removedFromEntityManager(IECSManager entitymgr) {
		entities = null;
	}
	
	public abstract void update(IECSManager entityManager, float deltaTime);
	    
}
