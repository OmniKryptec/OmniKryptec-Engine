package de.omnikryptec.ecs;

import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.impl.Family;
import de.omnikryptec.ecs.system.GroupParallelComponentSystem;
import de.omnikryptec.ecs.system.IterativeComponentSystem;

public class DoSomethingSystem extends GroupParallelComponentSystem{

	protected DoSomethingSystem() {
		super(Family.of(ComponentType.of(SomeDataComponent.class)), 8, 0);
	}

	private ComponentMapper<SomeDataComponent> mapper = new ComponentMapper<>(SomeDataComponent.class);

	@Override
	public void updateIndividual(IECSManager manager, Entity entity, float deltaTime) {
		mapper.get(entity).alonglong++;
		entity.removeComponent(mapper.getType());
	}

}
