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

package de.omnikryptec.ecs;

import de.omnikryptec.ecs.component.Component;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.util.data.DynamicArray;

import java.util.BitSet;

public class Entity {
    
    public int flags;
    
    protected IECSManager iecsManager;
    
    protected DynamicArray<Component> componentsArray;
    protected BitSet components;
    
    public Entity() {
        this.componentsArray = new DynamicArray<>();
        this.components = new BitSet();
    }
    
    public Entity addComponent(final Component component) {
        return addComponent(ComponentType.of(component.getClass()), component);
    }
    
    public Entity addComponent(final ComponentType type, final Component component) {
        this.componentsArray.set(type.id, component);
        this.components.set(type.id);
        if (hasIECSManager()) {
            this.iecsManager.onEntityComponentsChanged(this);
        }
        return this;
    }
    
    public Entity removeComponent(final ComponentType type) {
        return removeComponent(type, null);
    }
    
    public Entity removeComponent(final ComponentType componentType, final Component component) {
        // Only remove if there exists a component of that type to remove
        if (this.components.get(componentType.id)) {
            this.componentsArray.set(componentType.id, null);
            this.components.clear(componentType.id);
            if (hasIECSManager()) {
                this.iecsManager.onEntityComponentsChanged(this);
            }
        }
        return this;
    }
    
    public <C extends Component> C getComponent(final ComponentType componentType) {
        return (C) this.componentsArray.get(componentType.id);
    }
    
    public boolean hasComponent(final ComponentType type) {
        return this.components.get(type.id);
    }
    
    public BitSet getComponents() {
        return this.components;
    }
    
    public boolean hasIECSManager() {
        return this.iecsManager != null;
    }
    
    public IECSManager getIECSManager() {
        return this.iecsManager;
    }
    
    public void onIECSManagerAdded(final IECSManager manager) {
        this.iecsManager = manager;
    }
    
    public void onIECSManagerRemoved(final IECSManager manager) {
        this.iecsManager = null;
    }
}
