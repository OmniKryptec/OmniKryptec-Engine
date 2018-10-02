package de.omnikryptec.ecs.systems;

import java.util.List;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.EntityManager;
import de.omnikryptec.ecs.Family;

public abstract class IterativeComponentSystem extends ComponentSystem implements IndividualUpdater{

	protected IterativeComponentSystem(Family required) {
		super(required);
	}
	
	@Override
	public final void update(EntityManager entityManager, List<Entity> entities, float deltaTime) {
		for(Entity e : entities) {
			updateIndividual(entityManager, e, deltaTime);
		}
	}
	
}
