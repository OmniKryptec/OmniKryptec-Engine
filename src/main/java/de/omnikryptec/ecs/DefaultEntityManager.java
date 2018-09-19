package de.omnikryptec.ecs;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import de.codemakers.base.logger.Logger;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DefaultEntityManager extends EntityManager {
    
    private final BiMap<Long, Entity> entities;
    private final Multimap<ComponentSystem, Entity> entitiesPerSystem;
    private final Set<ComponentSystem> componentSystems;
    
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
        if (entity == null) {
            return false;
        }
        boolean added = false;
        entities.put(entity.ID, entity);
        for (ComponentSystem componentSystem : componentSystems) {
            if (entity.getComponents().keySet().containsAll(componentSystem.usesComponentClasses())) {
                added = entitiesPerSystem.put(componentSystem, entity);
            }
        }
        return added;
    }
    
    @Override
    public boolean removeEntity(Entity entity) {
        if (entity == null) {
            return false;
        }
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
        if (componentSystem == null) {
            return false;
        }
        return componentSystems.add(componentSystem);
    }
    
    @Override
    public boolean removeComponentSystem(ComponentSystem componentSystem) {
        if (componentSystem == null) {
            return false;
        }
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
            try {
                componentSystem.update(this, entitiesPerSystem.get(componentSystem), deltaTime);
            } catch (Exception ex) {
                Logger.handleError(ex);
            }
        }
        updating.set(false);
    }
    
}
