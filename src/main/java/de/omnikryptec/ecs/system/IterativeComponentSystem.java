package de.omnikryptec.ecs.system;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.IECSManager;

import java.util.BitSet;

public abstract class IterativeComponentSystem extends ComponentSystem implements IndividualUpdater {
    
    protected IterativeComponentSystem(BitSet required) {
        super(required);
    }
    
    @Override
    public final void update(IECSManager entityManager, float deltaTime) {
        for (Entity e : entities) {
            updateIndividual(entityManager, e, deltaTime);
        }
    }
    
}
