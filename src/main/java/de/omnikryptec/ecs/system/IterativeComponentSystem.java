package de.omnikryptec.ecs.system;

import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.entity.Entity;
import de.omnikryptec.ecs.family.Family;

public abstract class IterativeComponentSystem extends ComponentSystem implements IndividualUpdater{

	protected IterativeComponentSystem(Family required) {
		super(required);
	}
	
	@Override
	public final void update(IECSManager entityManager, float deltaTime) {
		for(Entity e : entities) {
			updateIndividual(entityManager, e, deltaTime);
		}
	}
	
}
