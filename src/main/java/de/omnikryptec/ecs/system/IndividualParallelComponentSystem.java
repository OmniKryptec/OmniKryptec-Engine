package de.omnikryptec.ecs.system;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.util.ExecutorsUtil;

public abstract class IndividualParallelComponentSystem extends ParallelComponentSystem {

    public IndividualParallelComponentSystem(BitSet required) {
	this(required, ExecutorsUtil.getAvailableThreads(), ExecutorsUtil.getAvailableThreads() * 5);
    }

    public IndividualParallelComponentSystem(BitSet required, int threads, int activationSize) {
	super(required, threads, activationSize);
    }

    @Override
    public void updateThreaded(IECSManager entityManager, List<Entity> entities, float deltaTime) {
	final Collection<Callable<Void>> tasks = new ArrayList<>(entities.size());
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
