package de.omnikryptec.minigame;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.system.IterativeComponentSystem;
import de.omnikryptec.util.math.Mathf;
import de.omnikryptec.util.updater.Time;

public class RangedSystem extends IterativeComponentSystem {
    
    protected RangedSystem() {
        super(Family.of(RangedComponent.class, PositionComponent.class));
        
    }
    
    private ComponentMapper<RangedComponent> mapper = new ComponentMapper<>(RangedComponent.class);
    private ComponentMapper<PositionComponent> posM = new ComponentMapper<>(PositionComponent.class);
    
    @Override
    public void updateIndividual(IECSManager manager, Entity entity, Time time) {
        PositionComponent pos = posM.get(entity);
        RangedComponent w = mapper.get(entity);
        
        if (Mathf.square(pos.transform.wPosition().x() - w.startX) + Mathf.square(pos.transform.wPosition().y() - w.startY) > Mathf.square(w.maxrange)) {
            manager.removeEntity(entity);
            
            Minigame.BUS.post(new RangeMaxedEvent(entity));
        }
    }
    
}
