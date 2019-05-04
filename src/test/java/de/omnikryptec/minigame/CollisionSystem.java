package de.omnikryptec.minigame;

import org.joml.Intersectionf;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.ComponentSystem;
import de.omnikryptec.util.Profiler;
import de.omnikryptec.util.updater.Time;

public class CollisionSystem extends ComponentSystem {
    
    protected CollisionSystem() {
        super(Family.of(ComponentType.of(PositionComponent.class), ComponentType.of(CollisionComponent.class)));
        
    }
    
    private ComponentMapper<PositionComponent> posMapper = new ComponentMapper<>(PositionComponent.class);
    private ComponentMapper<CollisionComponent> hitMapper = new ComponentMapper<>(CollisionComponent.class);
    
    @Override
    public void update(IECSManager manager, Time time) {
        Profiler.begin("CollisionSystem");
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            for (int j = i + 1; j < entities.size(); j++) {
                Entity e = entities.get(j);
                CollisionComponent hit1 = hitMapper.get(e);
                CollisionComponent hit2 = hitMapper.get(entity);
                PositionComponent pos1 = posMapper.get(e);
                PositionComponent pos2 = posMapper.get(entity);
                boolean intersect = Intersectionf.testAabAab(pos1.pos.x, pos1.pos.y, 0, pos1.pos.x + hit1.w,
                        pos1.pos.y + hit1.h, 0, pos2.pos.x, pos2.pos.y, 0, pos2.pos.x + hit2.w, pos2.pos.y + hit2.h, 0);
                if (intersect) {
                    Minigame.BUS.post(new CollisionEvent(entity, e));
                }
            }
        }
        Profiler.end();
    }
    
}
