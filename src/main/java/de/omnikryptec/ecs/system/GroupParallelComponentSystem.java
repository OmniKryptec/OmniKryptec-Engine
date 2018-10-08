package de.omnikryptec.ecs.system;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.IECSManager;

public abstract class GroupParallelComponentSystem extends ParallelComponentSystem {

	public GroupParallelComponentSystem(BitSet required, int threads, int activationSize) {
		super(required, threads, activationSize);
	}

	@Override
	public void updateThreaded(IECSManager entityManager, List<Entity> entities, float deltaTime) {
		List<List<Entity>> lists = Lists.partition(entities, (int) Math.ceil(entities.size() / (double) numThreads()));
		Collection<Callable<Void>> tasks = new ArrayList<>();
		for (List<Entity> el : lists) {
			tasks.add(() -> {
				for (Entity e : el) {
					updateIndividual(entityManager, e, deltaTime);
				}
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
