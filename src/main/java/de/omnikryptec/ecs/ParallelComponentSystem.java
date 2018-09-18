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
            entities.parallelStream().map((entity) -> executorService.submit(() -> updateIndividual(entityManager, entity, dt))).forEach((future) -> {
                try {
                    future.get(1, TimeUnit.MINUTES);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
            /*
            Future<?> future = null;
            for (Entity entity : entities) {
                future = executorService.submit(() -> updateIndividual(entityManager, entity, dt));
            }
            if (future != null) {
                try {
                    future.get(1, TimeUnit.MINUTES);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            */
            //Better reuse the Executioner...
            //            try {
            //                executorService.shutdown();
            //                executorService.awaitTermination(1, TimeUnit.MINUTES);
            //            } catch (InterruptedException ex) {
            //                throw new RuntimeException(ex);
            //            }
        }
    }
    
    public abstract void updateIndividual(EntityManager entityManager, Entity entity, float dt);
    
}
