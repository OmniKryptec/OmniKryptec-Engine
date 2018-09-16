package de.omnikryptec.ecs;

import java.util.Collection;
import java.util.List;

public abstract class ComponentSystem extends StaticClassHashObject{

	public abstract void update(EntityManager mgr, Collection<Entity> entities, float dt);
}
