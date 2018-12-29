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

package de.omnikryptec.ecs.impl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.EntityListener;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.system.ComponentSystem;
import de.omnikryptec.util.updater.Time;

/**
 * The default implementation of {@link IECSManager}.
 *
 * @author pcfreak9000
 */
public class ECSManager implements IECSManager {
    
    private final EntityManager entityManager;
    private final SystemManager systemManager;
    
    private final Collection<EntityListener> listeners;
    
    private boolean updating = false;
    private Queue<ECSSystemTask> systemTasks;
    private Queue<ECSEntityTask> entityTasks;
    
    public ECSManager() {
        this(false);
    }
    
    public ECSManager(final boolean concurrent) {
        this(concurrent, new EntityManager(), new SystemManager());
    }
    
    public ECSManager(final boolean concurrent, final EntityManager entityManager, final SystemManager systemManager) {
        if (concurrent) {
            this.systemTasks = new ConcurrentLinkedQueue<>();
            this.entityTasks = new ConcurrentLinkedQueue<>();
        } else {
            this.systemTasks = new ArrayDeque<>();
            this.entityTasks = new ArrayDeque<>();
        }
        this.entityManager = entityManager;
        this.systemManager = systemManager;
        this.listeners = new ArrayList<>();
    }
    
    @Override
    public void addEntity(final Entity entity) {
        if (this.updating) {
            this.entityTasks.add(new ECSEntityTask(entity, ECSTaskType.ADD));
        } else {
            addEntityInt(entity);
        }
    }
    
    private void addEntityInt(final Entity e) {
        e.onIECSManagerAdded(this);
        this.entityManager.addEntity(e);
        for (final EntityListener l : this.listeners) {
            l.entityAdded(e);
        }
    }
    
    @Override
    public void removeEntity(final Entity entity) {
        if (this.updating) {
            this.entityTasks.add(new ECSEntityTask(entity, ECSTaskType.REMOVE));
        } else {
            remEntityInt(entity);
        }
    }
    
    private void remEntityInt(final Entity e) {
        e.onIECSManagerRemoved(this);
        this.entityManager.removeEntity(e);
        for (final EntityListener l : this.listeners) {
            l.entityRemoved(e);
        }
    }
    
    @Override
    public void addSystem(final ComponentSystem system) {
        if (this.updating) {
            this.systemTasks.add(new ECSSystemTask(system, ECSTaskType.ADD));
        } else {
            addSysInt(system);
        }
    }
    
    private void addSysInt(final ComponentSystem system) {
        this.systemManager.addSystem(system);
        if (!system.getFamily().isEmpty()) {
            this.entityManager.addFilter(system.getFamily());
        }
        system.addedToIECSManager(this);
    }
    
    @Override
    public void removeSystem(final ComponentSystem system) {
        if (this.updating) {
            this.systemTasks.add(new ECSSystemTask(system, ECSTaskType.REMOVE));
        } else {
            remSysInt(system);
        }
    }
    
    private void remSysInt(final ComponentSystem system) {
        this.systemManager.removeSystem(system);
        if (!system.getFamily().isEmpty()) {
            this.entityManager.removeFilter(system.getFamily());
        }
        system.removedFromIECSManager(this);
    }
    
    @Override
    public List<Entity> getEntitesFor(final BitSet f) {
        return this.entityManager.getEntitiesFor(f);
    }
    
    @Override
    public void onEntityComponentsChanged(final Entity entity) {
        if (this.updating) {
            this.entityTasks.add(new ECSEntityTask(entity, ECSTaskType.ENTITY_TYPE_CHANGED));
        } else {
            this.entityManager.updateEntityFamilyStatus(entity);
        }
    }
    
    @Override
    public void update(final Time time) {
        this.updating = true;
        final Collection<ComponentSystem> systems = this.systemManager.getAll();
        for (final ComponentSystem system : systems) {
            if (system.isEnabled()) {
                system.update(this, time);
                runTasks();
            }
        }
        this.updating = false;
    }
    
    private void runTasks() {
        while (!this.systemTasks.isEmpty()) {
            final ECSSystemTask t = this.systemTasks.poll();
            switch (t.type) {
            case ADD:
                addSysInt(t.system);
                break;
            case REMOVE:
                remSysInt(t.system);
                break;
            default:
                throw new IllegalArgumentException("Wrong or unexpected type for systemtask: " + t.type);
            }
        }
        while (!this.entityTasks.isEmpty()) {
            final ECSEntityTask t = this.entityTasks.poll();
            switch (t.type) {
            case ADD:
                addEntityInt(t.entity);
                break;
            case REMOVE:
                remEntityInt(t.entity);
                break;
            case ENTITY_TYPE_CHANGED:
                this.entityManager.updateEntityFamilyStatus(t.entity);
                break;
            default:
                throw new IllegalArgumentException("Wrong or unexpected type for entitytask: " + t.type);
            }
        }
    }
    
    public boolean isUpdating() {
        return this.updating;
    }
    
    @Override
    public Collection<Entity> getAll() {
        return this.entityManager.getAll();
    }
    
    @Override
    public void addEntityListener(final BitSet family, final EntityListener listener) {
        if (family != null) {
            this.entityManager.addEntityListener(family, listener);
        } else {
            this.listeners.add(listener);
        }
    }
    
    @Override
    public void removeEntityListener(final BitSet family, final EntityListener listener) {
        if (family != null) {
            this.entityManager.removeEnityListener(family, listener);
        } else {
            this.listeners.remove(listener);
        }
    }
    
    private static enum ECSTaskType {
        REMOVE, ADD, ENTITY_TYPE_CHANGED;
    }
    
    private static class ECSSystemTask {
        
        ComponentSystem system;
        ECSTaskType type;
        
        private ECSSystemTask(final ComponentSystem sys, final ECSTaskType t) {
            this.system = sys;
            this.type = t;
        }
    }
    
    private static class ECSEntityTask {
        
        Entity entity;
        ECSTaskType type;
        
        private ECSEntityTask(final Entity e, final ECSTaskType t) {
            this.entity = e;
            this.type = t;
        }
    }
}
