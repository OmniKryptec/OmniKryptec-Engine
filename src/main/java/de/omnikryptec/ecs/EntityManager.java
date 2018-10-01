/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import de.omnikryptec.ecs.systems.ComponentSystem;

public abstract class EntityManager {
    
    protected final AtomicBoolean updating = new AtomicBoolean(false);
    
    public abstract long getId(Entity entity);
    
    public abstract Entity getEntity(long id);
    
    public abstract boolean addEntity(Entity entity);
    
    public abstract boolean removeEntity(Entity entity);
    
    public abstract boolean removeEntity(long id);
    
    public abstract Collection<Entity> getEntities();
    
    public abstract boolean addComponentSystem(ComponentSystem componentSystem);
    
    public abstract boolean removeComponentSystem(ComponentSystem componentSystem);
    
    public abstract Collection<ComponentSystem> getComponentSystems();
    
    public abstract void update(float deltaTime);
    
    public boolean isUpdating() {
        return updating.get();
    }
    
}
