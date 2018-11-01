package de.omnikryptec.ecs.impl;

import de.omnikryptec.ecs.system.ComponentSystem;

import java.util.*;

public class SystemManager {
    
    private static final Comparator<ComponentSystem> COMPARATOR = new Comparator<ComponentSystem>() {
        
        @Override
        public int compare(ComponentSystem o1, ComponentSystem o2) {
            return o2.priority() - o1.priority();
        }
    };
    
    private List<ComponentSystem> systems;
    private Collection<ComponentSystem> unmodifiableSystems;
    
    public SystemManager() {
        this.systems = new ArrayList<>();
        this.unmodifiableSystems = Collections.unmodifiableCollection(this.systems);
    }
    
    public SystemManager addSystem(ComponentSystem system) {
        systems.add(system);
        Collections.sort(systems, COMPARATOR);
        return this;
    }
    
    public SystemManager removeSystem(ComponentSystem system) {
        systems.remove(system);
        return this;
    }
    
    public Collection<ComponentSystem> getAll() {
        return unmodifiableSystems;
    }
    
}
