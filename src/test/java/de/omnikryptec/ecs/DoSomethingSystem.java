package de.omnikryptec.ecs;

import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.IterativeComponentSystem;

public class DoSomethingSystem extends IterativeComponentSystem{

	protected DoSomethingSystem() {
		super(Family.of(ComponentType.of(SomeDataComponent.class)));
	}

	private ComponentMapper<SomeDataComponent> mapper = new ComponentMapper<>(SomeDataComponent.class);

	@Override
	public void updateIndividual(IECSManager manager, Entity entity, float deltaTime) {
		mapper.get(entity).alonglong++;
		//manager.removeEntity(entity);
		//entity.removeComponent(mapper.getType());
	}

}
