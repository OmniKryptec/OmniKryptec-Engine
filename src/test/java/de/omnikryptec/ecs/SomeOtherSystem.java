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

import de.omnikryptec.ecs.system.ComponentSystem;
import de.omnikryptec.util.updater.Time;

import java.util.BitSet;

public class SomeOtherSystem extends ComponentSystem {
    
    protected SomeOtherSystem() {
        super(new BitSet());
    }
    
    @Override
    public void update(final IECSManager entityManager, final Time deltaTime) {
        // System.out.println("ma friends!");
    }
    
    @Override
    public int priority() {
        return 100;
    }
    
}
