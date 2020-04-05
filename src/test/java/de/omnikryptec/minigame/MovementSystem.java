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
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.IterativeComponentSystem;
import de.omnikryptec.util.updater.Time;

public class MovementSystem extends IterativeComponentSystem {

    protected MovementSystem() {
        super(Family.of(ComponentType.of(PositionComponent.class), ComponentType.of(MovementComponent.class)));
    }

    private final ComponentMapper<PositionComponent> posMapper = new ComponentMapper<>(PositionComponent.class);
    private final ComponentMapper<MovementComponent> movMapper = new ComponentMapper<>(MovementComponent.class);

    private final ComponentMapper<PlayerComponent> playMapper = new ComponentMapper<>(PlayerComponent.class);

    @Override
    public void updateIndividual(final IECSManager manager, final Entity entity, final Time time) {
        final PositionComponent pos = this.posMapper.get(entity);
        final MovementComponent mov = this.movMapper.get(entity);
        pos.transform.localspaceWrite().translate(mov.dx * time.deltaf, mov.dy * time.deltaf);
        pos.transform.revalidate();
        if (entity.hasComponent(this.playMapper.getType())) {
            RendererSystem.CAMERA.getTransform().localspaceWrite().setTranslation(-pos.transform.worldspacePos().x(),
                    -pos.transform.worldspacePos().y(), 0);
            Omnikryptec.getAudio().setListenerPosition(pos.transform.worldspacePos().x() / 20,
                    pos.transform.worldspacePos().y() / 20, -5);
            Omnikryptec.getAudio().setListenerVelocity(mov.dx / 20, mov.dy / 20, 0);
        }
    }

    @Override
    public int priority() {

        return -10;
    }

}
