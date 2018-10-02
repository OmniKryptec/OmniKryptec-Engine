package de.omnikryptec.ecs.systems;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.EntityManager;

public interface IndividualUpdater {
	
	void updateIndividual(EntityManager manager, Entity entity, float deltaTime);
}
