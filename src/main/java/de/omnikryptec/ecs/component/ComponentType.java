package de.omnikryptec.ecs.component;

import java.util.HashMap;
import java.util.Map;

public class ComponentType {
    
    private static int index = 0;
    private static Map<Class<? extends Component>, ComponentType> componentTypes = new HashMap<>();
    private int id;
    
    private ComponentType() {
        id = index++;
    }
    
    public static ComponentType of(Class<? extends Component> clazz) {
        if (!componentTypes.containsKey(clazz)) {
            synchronized (componentTypes) {
                componentTypes.put(clazz, new ComponentType());
            }
        }
        return componentTypes.get(clazz);
    }
    
    public static ComponentType ofExisting(Class<? extends Component> clazz) {
        return componentTypes.get(clazz);
    }
    
    public static int getTypes() {
        return index;
    }
    
    public int getId() {
        return id;
    }
    
    @Override
    public int hashCode() {
        return id;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof ComponentType) {
            if (id == ((ComponentType) obj).id) {
                return true;
            }
        }
        return false;
    }
    
}
