package de.omnikryptec.ecs;

import de.omnikryptec.util.Util;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class ParallelComponentSystem extends ComponentSystem {
    
    private ExecutorService executorService;
    private int activationSize;
    
    public ParallelComponentSystem(int threads, int activationSize) {
        this.executorService = Executors.newFixedThreadPool(threads);
        this.activationSize = activationSize;
    }
    
    public ParallelComponentSystem(ExecutorService executorService, int activationSize) {
        Util.ensureNonNull(executorService, "ExecutorService must not be null!");
        this.executorService = executorService;
        this.activationSize = activationSize;
    }
    
    @Override
    public void update(EntityManager entityManager, Collection<Entity> entities, float dt) {
        if (entities.size() < activationSize) {
            for (Entity entity : entities) {
                updateIndividual(entityManager, entity, dt);
            }
        } else {
            for (Entity entity : entities) {
                executorService.submit(() -> updateIndividual(entityManager, entity, dt));
            }
            try {
                executorService.shutdown();
                executorService.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    
    public abstract void updateIndividual(EntityManager entityManager, Entity entity, float dt);
    
}
