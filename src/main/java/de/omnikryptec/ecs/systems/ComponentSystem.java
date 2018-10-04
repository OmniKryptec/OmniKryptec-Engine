package de.omnikryptec.ecs.systems;

import java.util.List;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IEntityManager;

public abstract class ComponentSystem {
    
	private Family family;
	
	protected ComponentSystem(Family required) {
		this.family = required;
	}
	
	public Family getFamily() {
		return family;
	}

	public abstract void update(IEntityManager entityManager, List<Entity> entities, float deltaTime);
	    
}
