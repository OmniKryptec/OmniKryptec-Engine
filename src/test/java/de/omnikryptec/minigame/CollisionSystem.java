package de.omnikryptec.minigame;

import org.joml.Intersectionf;

import de.omnikryptec.core.Omnikryptec;
import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.AbstractComponentSystem;
import de.omnikryptec.util.Profiler;
import de.omnikryptec.util.updater.Time;

public class CollisionSystem extends AbstractComponentSystem {
    
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
                boolean intersect = Intersectionf.testAabAab(pos1.transform.worldspacePos().x(),
                        pos1.transform.worldspacePos().y(), 0, pos1.transform.worldspacePos().x() + hit1.w,
                        pos1.transform.worldspacePos().y() + hit1.h, 0, pos2.transform.worldspacePos().x(),
                        pos2.transform.worldspacePos().y(), 0, pos2.transform.worldspacePos().x() + hit2.w,
                        pos2.transform.worldspacePos().y() + hit2.h, 0);
                if (intersect) {
                    Omnikryptec.getEventBus().post(new CollisionEvent(entity, e));
                }
            }
        }
        Profiler.end();
    }
    
}
