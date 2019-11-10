package de.omnikryptec.minigame;

import de.omnikryptec.core.Omnikryptec;
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

    private final ComponentMapper<RangedComponent> mapper = new ComponentMapper<>(RangedComponent.class);
    private final ComponentMapper<PositionComponent> posM = new ComponentMapper<>(PositionComponent.class);

    @Override
    public void updateIndividual(final IECSManager manager, final Entity entity, final Time time) {
        final PositionComponent pos = this.posM.get(entity);
        final RangedComponent w = this.mapper.get(entity);

        if (Mathf.square(pos.transform.worldspacePos().x() - w.startX)
                + Mathf.square(pos.transform.worldspacePos().y() - w.startY) > Mathf.square(w.maxrange)) {
            manager.removeEntity(entity);

            Omnikryptec.getEventBus().post(new RangeMaxedEvent(entity));
        }
    }

}
