package de.omnikryptec.ecs;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DefaultEntityManager extends EntityManager {
    
    private BiMap<Long, Entity> entities;
    private Multimap<ComponentSystem, Entity> entitiesPerSystem;
    private Set<ComponentSystem> componentSystems;
    
    public DefaultEntityManager() {
        entities = HashBiMap.create();
        entitiesPerSystem = ArrayListMultimap.create();
        componentSystems = new HashSet<>();
    }
    
    @Override
    public long getId(Entity entity) {
        return entities.inverse().get(entity);
    }
    
    @Override
    public Entity getEntity(long id) {
        return entities.get(id);
    }
    
    @Override
    public boolean addEntity(Entity entity) {
        boolean added = false;
        for (ComponentSystem componentSystem : componentSystems) {
            if (entity.getComponents().keySet().containsAll(componentSystem.usesComponentClasses())) {
                added = entitiesPerSystem.put(componentSystem, entity);
            }
        }
        return added;
    }
    
    @Override
    public boolean removeEntity(Entity entity) {
        entities.remove(entity);
        return entitiesPerSystem.keySet().stream().anyMatch((componentSystem) -> entitiesPerSystem.remove(componentSystem, entity));
    }
    
    @Override
    public boolean removeEntity(long id) {
        return removeEntity(getEntity(id));
    }
    
    @Override
    public Collection<Entity> getEntities() {
        return entities.values();
    }
    
    @Override
    public boolean addComponentSystem(ComponentSystem componentSystem) {
        return componentSystems.add(componentSystem);
    }
    
    @Override
    public boolean removeComponentSystem(ComponentSystem componentSystem) {
        entitiesPerSystem.removeAll(componentSystem);
        return componentSystems.remove(componentSystem);
    }
    
    @Override
    public Collection<ComponentSystem> getComponentSystems() {
        return componentSystems;
    }
    
    @Override
    public void update(float deltaTime) {
        if (isUpdating()) {
            throw new IllegalStateException(getClass().getSimpleName() + " is already updating!");
        }
        updating.set(true);
        for (ComponentSystem componentSystem : componentSystems) {
            componentSystem.update(this, entitiesPerSystem.get(componentSystem), deltaTime);
        }
        updating.set(false);
    }
    
}
