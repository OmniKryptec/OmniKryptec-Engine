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

package de.omnikryptec.ecs.impl;

import de.omnikryptec.ecs.system.AbstractComponentSystem;

import java.util.*;

public class SystemManager {
    
    private static final Comparator<AbstractComponentSystem> COMPARATOR = (o1, o2) -> o2.priority() - o1.priority();
    
    private final List<AbstractComponentSystem> systems;
    private final Collection<AbstractComponentSystem> unmodifiableSystems;
    
    public SystemManager() {
        this.systems = new ArrayList<>();
        this.unmodifiableSystems = Collections.unmodifiableCollection(this.systems);
    }
    
    public SystemManager addSystem(final AbstractComponentSystem system) {
        this.systems.add(system);
        Collections.sort(this.systems, COMPARATOR);
        return this;
    }
    
    public SystemManager removeSystem(final AbstractComponentSystem system) {
        this.systems.remove(system);
        return this;
    }
    
    public Collection<AbstractComponentSystem> getAll() {
        return this.unmodifiableSystems;
    }
    
}
