package de.omnikryptec.minigame;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.IterativeComponentSystem;
import de.omnikryptec.util.updater.Time;

public class MovementSystem extends IterativeComponentSystem {
    
    protected MovementSystem() {
        super(Family.of(ComponentType.of(PositionComponent.class), ComponentType.of(MovementComponent.class)));
        
    }
    
    private final ComponentMapper<PositionComponent> posMapper = new ComponentMapper<>(PositionComponent.class);
    private final ComponentMapper<MovementComponent> movMapper = new ComponentMapper<>(MovementComponent.class);
    
    @Override
    public void updateIndividual(final IECSManager manager, final Entity entity, final Time time) {
        final PositionComponent pos = this.posMapper.get(entity);
        final MovementComponent mov = this.movMapper.get(entity);
        pos.transform.localspaceWrite().translate(mov.dx * time.deltaf, mov.dy * time.deltaf);
        pos.transform.revalidate();
    }
    
    @Override
    public int priority() {
        
        return -10;
    }
    
}
