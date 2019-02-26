package de.omnikryptec.minigame;

import java.util.BitSet;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.system.IterativeComponentSystem;
import de.omnikryptec.util.updater.Time;

public class RangedWeaponSystem extends IterativeComponentSystem{

    protected RangedWeaponSystem() {
        super(Family.of(RangedWeaponComponent.class, PositionComponent.class));
        
    }
    
    private ComponentMapper<RangedWeaponComponent> mapper = new ComponentMapper<>(RangedWeaponComponent.class);
    private ComponentMapper<PositionComponent> posM = new ComponentMapper<>(PositionComponent.class);
    
    @Override
    public void updateIndividual(IECSManager manager, Entity entity, Time time) {
        PositionComponent pos = posM.get(entity);
        RangedWeaponComponent w = mapper.get(entity);
        //if(())
    }
    
    
    
}
