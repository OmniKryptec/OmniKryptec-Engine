package de.omnikryptec.minigame;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.system.AbstractComponentSystem;
import de.omnikryptec.util.updater.Time;

public class RandomColorSystem extends AbstractComponentSystem {
    
    private ComponentMapper<RenderComponent> rcm = new ComponentMapper<>(RenderComponent.class);
    
    public RandomColorSystem() {
        super(Family.of(RenderComponent.class, RandomColorComponent.class));
    }
    
    private float last = 0;
    
    @Override
    public void update(IECSManager iecsManager, Time time) {
        if (time.currentf - last > 1) {
            for (Entity entity : entities) {
                rcm.get(entity).color.randomizeRGB();
            }
            last = time.currentf;
        }
    }
    
}
