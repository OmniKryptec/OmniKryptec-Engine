package de.omnikryptec.minigame;

import org.joml.Intersectionf;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.ComponentSystem;
import de.omnikryptec.event.Event;
import de.omnikryptec.util.updater.Time;

public class CollisionSystem extends ComponentSystem {
    
    protected CollisionSystem() {
        super(Family.of(ComponentType.of(PositionComponent.class), ComponentType.of(HitBoxComponent.class)));
        
    }
    
    private ComponentMapper<PositionComponent> posMapper = new ComponentMapper<>(PositionComponent.class);
    private ComponentMapper<HitBoxComponent> hitMapper = new ComponentMapper<>(HitBoxComponent.class);
    
    @Override
    public void update(IECSManager manager, Time time) {
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            for (int j = i + 1; j < entities.size(); j++) {
                Entity e = entities.get(j);
                HitBoxComponent hit1 = hitMapper.get(e);
                HitBoxComponent hit2 = hitMapper.get(entity);
                PositionComponent pos1 = posMapper.get(e);
                PositionComponent pos2 = posMapper.get(entity);
                boolean intersect = Intersectionf.testAabAab(pos1.x, pos1.y, 0, pos1.x + hit1.w, pos1.y + hit1.h, 0,
                        pos2.x, pos2.y, 0, pos2.x + hit2.w, pos2.y + hit2.h, 0);
                if (intersect) {
                    Minigame.BUS.post(new CollisionEvent(entity, e));
                }
            }
        }
    }
    
    public static class CollisionEvent extends Event {
        public final Entity[] colliding = new Entity[2];
        
        public CollisionEvent(Entity e1, Entity e2) {
            colliding[0] = e1;
            colliding[1] = e2;
        }
        
    }
    
}
