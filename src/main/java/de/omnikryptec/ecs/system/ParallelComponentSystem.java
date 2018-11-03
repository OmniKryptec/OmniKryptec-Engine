/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.ecs.system;

import de.omnikryptec.core.Time;
import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.util.ExecutorsUtil;

import java.util.BitSet;
import java.util.List;
import java.util.concurrent.ExecutorService;

public abstract class ParallelComponentSystem extends ComponentSystem implements IndividualUpdater {
    
    private ExecutorService executorService;
    private int size;
    private int activationSize;
    
    public ParallelComponentSystem(BitSet required, int threads, int activationSize) {
        super(required);
        this.size = threads;
        this.activationSize = activationSize;
        this.executorService = ExecutorsUtil.newFixedThreadPool(threads);
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
    public final void update(IECSManager entityManager, Time time) {
        if (entities.size() > 0) {
            if (entities.size() < activationSize) {
                for (Entity e : entities) {
                    updateIndividual(entityManager, e, time);
                }
            } else {
                updateThreaded(entityManager, entities, time);
            }
        }
    }
    
    public abstract void updateThreaded(IECSManager entityManager, List<Entity> entities, Time time);
    
}
