package de.omnikryptec.ecs;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class EntityManager {
    
    private final AtomicBoolean updating = new AtomicBoolean(false);
    private BiMap<Long, Entity> entities;
    private Multimap<ComponentSystem, Entity> entitiesPerSystem;
    private Set<ComponentSystem> componentSystems;
    
    public EntityManager() {
        entities = HashBiMap.create();
        entitiesPerSystem = ArrayListMultimap.create();
        componentSystems = new HashSet<>();
    }
    
    public long getEntityId(Entity entity) {
        return entities.inverse().get(entity);
    }
    
    public Entity getEntity(long id) {
        return entities.get(id);
    }
    
    public void addEntity(Entity entity) {
        for (ComponentSystem componentSystem : componentSystems) {
            if (entity.getComponents().keySet().containsAll(componentSystem.usesComponentClasses())) {
                entitiesPerSystem.put(componentSystem, entity);
            }
        }
    }
    
    public void addSystem(ComponentSystem componentSystem) {
        componentSystems.add(componentSystem);
    }
    
    public void updateSystems(float deltaTime) {
        if (isUpdating()) {
            throw new IllegalStateException(getClass().getSimpleName() + " is already updating!");
        }
        updating.set(true);
        for (ComponentSystem componentSystem : componentSystems) {
            componentSystem.update(this, entitiesPerSystem.get(componentSystem), deltaTime);
        }
        updating.set(false);
    }
    
    public boolean isUpdating() {
        return updating.get();
    }
}
