package de.omnikryptec.ecs;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;

import java.util.HashSet;
import java.util.Set;

public class EntityManager {
    
    private BiMap<Long, Entity> entities;
    private Multimap<ComponentSystem, Entity> entitiesPerSystem;
    //private Multimap<Integer, ComponentSystem> systemsPerComponentType;
    //private Multimap<ComponentSystem, Integer> componentTypesPerSystem;
    private Set<ComponentSystem> componentSystems;
    
    private boolean updating = false;
    
    public EntityManager() {
        entities = HashBiMap.create();
        entitiesPerSystem = ArrayListMultimap.create();
        //systemsPerComponentType = ArrayListMultimap.create();
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
            //if(entity.getComponents().keySet().containsAll(componentSystem.usesComponents())) {
            if (entity.getComponentClasses().containsAll(componentSystem.usesComponentClasses())) {
                entitiesPerSystem.put(componentSystem, entity);
            }
        }
    }
    
    public void addSystem(ComponentSystem componentSystem) {
        componentSystems.add(componentSystem);
        //		for(Integer id : componentSystem.usesComponents()) {
        //			systemsPerComponentType.put(id, componentSystem);
        //		}
    }
    
    public void updateSystems(float dt) {
        if (isUpdating()) {
            throw new IllegalStateException("Already updating!");
        }
        updating = true;
        for (ComponentSystem componentSystem : componentSystems) {
            componentSystem.update(this, entitiesPerSystem.get(componentSystem), dt);
        }
        updating = false;
    }
    
    public boolean isUpdating() {
        return updating;
    }
}
