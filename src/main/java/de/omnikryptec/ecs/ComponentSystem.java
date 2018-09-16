package de.omnikryptec.ecs;

import java.util.Collection;

public abstract class ComponentSystem extends StaticClassHashObject {
    
    public abstract void update(EntityManager mgr, Collection<Entity> entities, float dt);
    
    public abstract Collection<Integer> usesComponents();
    
    public abstract Collection<Class<? extends Component>> usesComponentClasses();
    
}
