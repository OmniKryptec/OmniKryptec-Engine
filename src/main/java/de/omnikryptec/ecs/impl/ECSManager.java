package de.omnikryptec.ecs.impl;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.EntityListener;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.system.ComponentSystem;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The default implementation of {@link IECSManager}.
 *
 * @author pcfreak9000
 */
public class ECSManager implements IECSManager {
    
    private EntityManager entityManager;
    private SystemManager systemManager;
    
    private Collection<EntityListener> listeners;
    
    private boolean updating = false;
    private Queue<ECSSystemTask> systemTasks;
    private Queue<ECSEntityTask> entityTasks;
    
    public ECSManager() {
        this(false);
    }
    
    public ECSManager(boolean concurrent) {
        this(concurrent, new EntityManager(), new SystemManager());
    }
    
    public ECSManager(boolean concurrent, EntityManager entityManager, SystemManager systemManager) {
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
    public void addEntity(Entity entity) {
        if (updating) {
            entityTasks.add(new ECSEntityTask(entity, ECSTaskType.ADD));
        } else {
            addEntityInt(entity);
        }
    }
    
    private void addEntityInt(Entity e) {
        e.onIECSManagerAdded(this);
        entityManager.addEntity(e);
        for (EntityListener l : listeners) {
            l.entityAdded(e);
        }
    }
    
    @Override
    public void removeEntity(Entity entity) {
        if (updating) {
            entityTasks.add(new ECSEntityTask(entity, ECSTaskType.REMOVE));
        } else {
            remEntityInt(entity);
        }
    }
    
    private void remEntityInt(Entity e) {
        e.onIECSManagerRemoved(this);
        entityManager.removeEntity(e);
        for (EntityListener l : listeners) {
            l.entityRemoved(e);
        }
    }
    
    @Override
    public void addSystem(ComponentSystem system) {
        if (updating) {
            systemTasks.add(new ECSSystemTask(system, ECSTaskType.ADD));
        } else {
            addSysInt(system);
        }
    }
    
    private void addSysInt(ComponentSystem system) {
        systemManager.addSystem(system);
        if (!system.getFamily().isEmpty()) {
            entityManager.addFilter(system.getFamily());
        }
        system.addedToIECSManager(this);
    }
    
    @Override
    public void removeSystem(ComponentSystem system) {
        if (updating) {
            systemTasks.add(new ECSSystemTask(system, ECSTaskType.REMOVE));
        } else {
            remSysInt(system);
        }
    }
    
    private void remSysInt(ComponentSystem system) {
        systemManager.removeSystem(system);
        if (!system.getFamily().isEmpty()) {
            entityManager.removeFilter(system.getFamily());
        }
        system.removedFromIECSManager(this);
    }
    
    @Override
    public List<Entity> getEntitesFor(BitSet f) {
        return entityManager.getEntitiesFor(f);
    }
    
    @Override
    public void onEntityComponentsChanged(Entity entity) {
        if (updating) {
            entityTasks.add(new ECSEntityTask(entity, ECSTaskType.ENTITY_TYPE_CHANGED));
        } else {
            entityManager.updateEntityFamilyStatus(entity);
        }
    }
    
    @Override
    public void update(float deltaTime) {
        updating = true;
        Collection<ComponentSystem> systems = systemManager.getAll();
        for (ComponentSystem system : systems) {
            if (system.isEnabled()) {
                system.update(this, deltaTime);
                runTasks();
            }
        }
        updating = false;
    }
    
    private void runTasks() {
        while (!systemTasks.isEmpty()) {
            ECSSystemTask t = systemTasks.poll();
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
        while (!entityTasks.isEmpty()) {
            ECSEntityTask t = entityTasks.poll();
            switch (t.type) {
                case ADD:
                    addEntityInt(t.entity);
                    break;
                case REMOVE:
                    remEntityInt(t.entity);
                    break;
                case ENTITY_TYPE_CHANGED:
                    entityManager.updateEntityFamilyStatus(t.entity);
                    break;
                default:
                    throw new IllegalArgumentException("Wrong or unexpected type for entitytask: " + t.type);
            }
        }
    }
    
    public boolean isUpdating() {
        return updating;
    }
    
    @Override
    public Collection<Entity> getAll() {
        return entityManager.getAll();
    }
    
    @Override
    public void addEntityListener(BitSet family, EntityListener listener) {
        if (family != null) {
            entityManager.addEntityListener(family, listener);
        } else {
            listeners.add(listener);
        }
    }
    
    @Override
    public void removeEntityListener(BitSet family, EntityListener listener) {
        if (family != null) {
            entityManager.removeEnityListener(family, listener);
        } else {
            listeners.remove(listener);
        }
    }
    
    private static enum ECSTaskType {
        REMOVE,
        ADD,
        ENTITY_TYPE_CHANGED;
    }
    
    private static class ECSSystemTask {
        
        ComponentSystem system;
        ECSTaskType type;
        
        private ECSSystemTask(ComponentSystem sys, ECSTaskType t) {
            this.system = sys;
            this.type = t;
        }
    }
    
    private static class ECSEntityTask {
        
        Entity entity;
        ECSTaskType type;
        
        private ECSEntityTask(Entity e, ECSTaskType t) {
            this.entity = e;
            this.type = t;
        }
    }
}
