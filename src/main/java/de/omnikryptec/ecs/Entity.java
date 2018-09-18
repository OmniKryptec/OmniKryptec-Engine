package de.omnikryptec.ecs;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

import java.util.Set;

public class Entity {
    
    public final long ID; //TODO Please make this lowercase // No, but maybe this will become unnecessary //Yes do it :(
    private ClassToInstanceMap<Component> components;
    
    public Entity() {
        this.ID = 0;
        this.components = MutableClassToInstanceMap.create();
    }
    
    public ClassToInstanceMap<Component> getComponents() {
        return components;
    }
    
    @Deprecated
    public Set<Class<? extends Component>> getComponentClasses() {
        return components.keySet();
    }
    
}
