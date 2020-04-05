/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
