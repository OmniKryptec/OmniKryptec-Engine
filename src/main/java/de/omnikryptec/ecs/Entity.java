package de.omnikryptec.ecs;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

import java.util.HashMap;
import java.util.Map;

public class Entity {
    
    public final long ID;
    private Map<Integer, Component> comps;
    private ClassToInstanceMap<Component> components = MutableClassToInstanceMap.create();
    
    public Entity() {
        this.ID = 0;
        this.comps = new HashMap<>();
    }
    
    public Map<Integer, Component> getComponents() {
        return comps;
    }
}
