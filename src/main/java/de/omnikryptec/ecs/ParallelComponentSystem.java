package de.omnikryptec.ecs;

import de.omnikryptec.util.Util;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class ParallelComponentSystem extends ComponentSystem {

	private ExecutorService service;
	private int activatorSize;

	public ParallelComponentSystem(int threads, int activatorsize) {
		this.service = Executors.newFixedThreadPool(threads);
		this.activatorSize = activatorsize;
	}

	public ParallelComponentSystem(ExecutorService service, int activatorSize) {
		Util.ensureNonNull(service, "ExecutorService must not be null!");
		this.service = service;
		this.activatorSize = activatorSize;
	}

	@Override
	public void update(EntityManager mgr, Collection<Entity> entities, float dt) {
		if (entities.size() < activatorSize) {
			for (Entity e : entities) {
				updateIndividual(mgr, e, dt);
			}
		} else {
			for (Entity e : entities) {
				service.submit(() -> updateIndividual(mgr, e, dt));
			}
			try {
				service.shutdown();
				service.awaitTermination(5, TimeUnit.DAYS);
			} catch (InterruptedException e1) {
				throw new RuntimeException(e1);
			}
		}
	}

	public abstract void updateIndividual(EntityManager mgr, Entity e, float dt);

}
