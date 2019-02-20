package de.omnikryptec.minigame;

import java.util.BitSet;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.IterativeComponentSystem;
import de.omnikryptec.util.updater.Time;

public class MovementSystem extends IterativeComponentSystem {
    
    protected MovementSystem() {
        super(Family.of(ComponentType.of(PositionComponent.class), ComponentType.of(MovementComp.class)));
        
    }
    
    private ComponentMapper<PositionComponent> posMapper = new ComponentMapper<>(PositionComponent.class);
    private ComponentMapper<MovementComp> movMapper = new ComponentMapper<>(MovementComp.class);
    
    @Override
    public void updateIndividual(IECSManager manager, Entity entity, Time time) {
        PositionComponent pos = posMapper.get(entity);
        MovementComp mov = movMapper.get(entity);
        pos.x += mov.dx * time.deltaf;
        pos.y += mov.dy * time.deltaf;
    }
    
}
