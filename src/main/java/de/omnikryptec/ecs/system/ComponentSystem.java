package de.omnikryptec.ecs.system;

import java.util.BitSet;
import java.util.List;

import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.entity.Entity;

public abstract class ComponentSystem {
    
	private BitSet family;
	protected List<Entity> entities;
	
	protected ComponentSystem(BitSet required) {
		this.family = required;
	}
	
	public BitSet getFamily() {
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
