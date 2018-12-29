package de.omnikryptec.ecs;

import java.util.BitSet;

import de.omnikryptec.ecs.system.ComponentSystem;
import de.omnikryptec.util.updater.Time;

public class AnotherSystem extends ComponentSystem {
    
    protected AnotherSystem() {
        super(new BitSet());
    }
    
    @Override
    public void update(final IECSManager entityManager, final Time deltaTime) {
        // System.out.println("Another System!");
    }
    
    @Override
    public int priority() {
        return -100;
    }
    
}
