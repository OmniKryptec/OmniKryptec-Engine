package de.omnikryptec.ecs.systems;

import java.util.Collection;
import java.util.List;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.EntityManager;
import de.omnikryptec.ecs.component.Component;

public abstract class ComponentSystem {
    
    public abstract void update(EntityManager entityManager, List<Entity> entities, float deltaTime);
        
    public abstract Collection<Class<? extends Component>> usesComponentClasses();
    
}
