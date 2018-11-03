package de.omnikryptec.ecs;

import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.GroupParallelComponentSystem;
import de.omnikryptec.util.updater.Time;

public class DoSomethingSystem extends GroupParallelComponentSystem{

	protected DoSomethingSystem() {
		super(Family.of(ComponentType.of(SomeDataComponent.class)));
	}

	private ComponentMapper<SomeDataComponent> mapper = new ComponentMapper<>(SomeDataComponent.class);

	@Override
	public void updateIndividual(IECSManager manager, Entity entity, Time deltaTime) {
		mapper.get(entity).alonglong++;
		//manager.removeEntity(entity);
		//entity.removeComponent(mapper.getType());
	}

}
