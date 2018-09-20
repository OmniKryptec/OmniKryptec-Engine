package de.omnikryptec.ecs;

import java.util.Collection;

import de.omnikryptec.ecs.component.Component;

public abstract class ComponentSystem extends StaticClassHashObject {
    
    public abstract void update(EntityManager entityManager, Collection<Entity> entities, float deltaTime);
        
    public abstract Collection<Class<? extends Component>> usesComponentClasses();
    
}
