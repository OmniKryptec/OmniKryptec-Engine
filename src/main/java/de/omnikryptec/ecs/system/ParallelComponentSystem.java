package de.omnikryptec.ecs.system;

import java.util.BitSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.entity.Entity;

public abstract class ParallelComponentSystem extends ComponentSystem implements IndividualUpdater{

	private ExecutorService executorService;
	private int size;
	private int activationSize;
	
	public ParallelComponentSystem(BitSet required, int threads, int activationSize) {
		super(required);
		this.size = threads;
		this.activationSize = activationSize;
		this.executorService = Executors.newFixedThreadPool(threads);
	}
	
	protected final ExecutorService getExecutor() {
		return executorService;
	}
	
	public int numThreads() {
		return size;
	}
	
	public int getActivationSize() {
		return activationSize;
	}
	
	@Override
	public final void update(IECSManager entityManager, float deltaTime) {
		if(entities.size()<activationSize) {
			for(Entity e : entities) {
				updateIndividual(entityManager, e, deltaTime);
			}
		}else {
			updateThreaded(entityManager, entities, deltaTime);
		}
	}
	
	public abstract void updateThreaded(IECSManager entityManager, List<Entity> entities, float deltaTime);
	
}
