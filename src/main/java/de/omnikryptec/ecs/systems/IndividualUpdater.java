package de.omnikryptec.ecs.systems;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.IEntityManager;

public interface IndividualUpdater {
	
	void updateIndividual(IEntityManager manager, Entity entity, float deltaTime);
}
