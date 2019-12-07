package de.omnikryptec.minigame;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.joml.Intersectionf;

import de.omnikryptec.core.Omnikryptec;
import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.AbstractComponentSystem;
import de.omnikryptec.util.ExecutorsUtil;
import de.omnikryptec.util.Profiler;
import de.omnikryptec.util.updater.Time;

public class CollisionSystem extends AbstractComponentSystem {
    
    protected CollisionSystem() {
        super(Family.of(ComponentType.of(PositionComponent.class), ComponentType.of(CollisionComponent.class)));
        
    }
    
    private final ComponentMapper<PositionComponent> posMapper = new ComponentMapper<>(PositionComponent.class);
    private final ComponentMapper<CollisionComponent> hitMapper = new ComponentMapper<>(CollisionComponent.class);
    
    private final ExecutorService service = ExecutorsUtil.newFixedThreadPool();
    
    @Override
    public void update(final IECSManager manager, final Time time) {
        Profiler.begin("CollisionSystem");
        final List<Callable<Void>> list = new ArrayList<>();
        for (int i = 0; i < this.entities.size(); i++) {
            final Entity entity = this.entities.get(i);
            final int ii = i;
            list.add(() -> {
                for (int j = ii + 1; j < this.entities.size(); j++) {
                    final Entity e = this.entities.get(j);
                    final CollisionComponent hit1 = this.hitMapper.get(e);
                    final CollisionComponent hit2 = this.hitMapper.get(entity);
                    final PositionComponent pos1 = this.posMapper.get(e);
                    final PositionComponent pos2 = this.posMapper.get(entity);
                    final boolean intersect = Intersectionf.testAabAab(pos1.transform.worldspacePos().x(),
                            pos1.transform.worldspacePos().y(), 0, pos1.transform.worldspacePos().x() + hit1.w,
                            pos1.transform.worldspacePos().y() + hit1.h, 0, pos2.transform.worldspacePos().x(),
                            pos2.transform.worldspacePos().y(), 0, pos2.transform.worldspacePos().x() + hit2.w,
                            pos2.transform.worldspacePos().y() + hit2.h, 0);
                    if (intersect) {
                        Omnikryptec.getEventBus().post(new CollisionEvent(entity, e));
                    }
                }
                return null;
            });
        }
        try {
            this.service.invokeAll(list);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        Profiler.end();
    }
    
}
