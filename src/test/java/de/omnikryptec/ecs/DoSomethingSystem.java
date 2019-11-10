/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.ecs;

import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.IterativeComponentSystem;
import de.omnikryptec.util.updater.Time;

public class DoSomethingSystem extends IterativeComponentSystem {

    protected DoSomethingSystem() {
        super(Family.of(ComponentType.of(SomeDataComponent.class)));
    }

    private final ComponentMapper<SomeDataComponent> mapper = new ComponentMapper<>(SomeDataComponent.class);

    @Override
    public void updateIndividual(final IECSManager manager, final Entity entity, final Time deltaTime) {
        this.mapper.get(entity).alonglong++;
        // manager.removeEntity(entity);
        // entity.removeComponent(mapper.getType());
    }

}
