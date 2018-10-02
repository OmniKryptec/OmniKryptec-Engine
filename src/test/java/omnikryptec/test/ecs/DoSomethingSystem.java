package omnikryptec.test.ecs;

import java.util.List;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.EntityManager;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.systems.ComponentSystem;

public class DoSomethingSystem extends ComponentSystem{

	protected DoSomethingSystem() {
		super(new Family(ComponentType.of(SomeDataComponent.class)));
	}

	private ComponentMapper<SomeDataComponent> mapper = new ComponentMapper<>(SomeDataComponent.class);
	
	@Override
	public void update(EntityManager entityManager, List<Entity> entities, float deltaTime) {
		for(Entity e : entities) {
			mapper.get(e).alonglong++;
		}
	}

}
