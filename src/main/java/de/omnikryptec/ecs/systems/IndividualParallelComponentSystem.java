package de.omnikryptec.ecs.systems;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.EntityManager;

public abstract class IndividualParallelComponentSystem extends ParallelComponentSystem {

	public IndividualParallelComponentSystem(int threads, int activationSize) {
		super(threads, activationSize);
	}

	@Override
	public void updateThreaded(EntityManager entityManager, List<Entity> entities, float deltaTime) {
		final Collection<Callable<Void>> tasks = new ArrayList<>();
		for (Entity entity : entities) {
			tasks.add(() -> {
				updateIndividual(entityManager, entity, deltaTime);
				return null;
			});
		}
		try {
			getExecutor().invokeAll(tasks, 1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
