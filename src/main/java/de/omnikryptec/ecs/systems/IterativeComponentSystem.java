package de.omnikryptec.ecs.systems;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IEntityManager;

public abstract class IterativeComponentSystem extends ComponentSystem implements IndividualUpdater{

	protected IterativeComponentSystem(Family required) {
		super(required);
	}
	
	@Override
	public final void update(IEntityManager entityManager, float deltaTime) {
		for(Entity e : entities) {
			updateIndividual(entityManager, e, deltaTime);
		}
	}
	
}
