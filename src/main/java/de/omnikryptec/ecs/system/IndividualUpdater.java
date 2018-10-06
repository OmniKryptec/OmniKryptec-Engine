package de.omnikryptec.ecs.system;

import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.entity.Entity;

public interface IndividualUpdater {
	
	void updateIndividual(IECSManager manager, Entity entity, float deltaTime);
}
