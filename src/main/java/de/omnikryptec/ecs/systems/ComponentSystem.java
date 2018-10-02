package de.omnikryptec.ecs.systems;

import java.util.List;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.EntityManager;
import de.omnikryptec.ecs.Family;

public abstract class ComponentSystem {
    
	private Family family;
	
	protected ComponentSystem(Family required) {
		this.family = required;
	}
	
	public Family getRequiredComponents() {
		return family;
	}
	
    public abstract void update(EntityManager entityManager, List<Entity> entities, float deltaTime);
    
}
