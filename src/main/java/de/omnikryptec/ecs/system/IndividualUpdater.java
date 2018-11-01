package de.omnikryptec.ecs.system;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.IECSManager;

public interface IndividualUpdater {
    
    void updateIndividual(IECSManager manager, Entity entity, float deltaTime);
    
}
