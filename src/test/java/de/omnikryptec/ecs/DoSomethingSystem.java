package de.omnikryptec.ecs;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.EntityManager;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.systems.GroupParallelComponentSystem;
import de.omnikryptec.ecs.systems.IndividualParallelComponentSystem;
import de.omnikryptec.ecs.systems.IterativeComponentSystem;

public class DoSomethingSystem extends IterativeComponentSystem{

	protected DoSomethingSystem() {
		super(new Family(ComponentType.of(SomeDataComponent.class)));
	}

	private ComponentMapper<SomeDataComponent> mapper = new ComponentMapper<>(SomeDataComponent.class);

	@Override
	public void updateIndividual(EntityManager manager, Entity entity, float deltaTime) {
		mapper.get(entity).alonglong++;
	}

}
