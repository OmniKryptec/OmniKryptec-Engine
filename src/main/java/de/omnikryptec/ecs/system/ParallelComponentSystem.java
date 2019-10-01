/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

import java.util.BitSet;
import java.util.List;
import java.util.concurrent.ExecutorService;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.util.ExecutorsUtil;
import de.omnikryptec.util.updater.Time;

@Deprecated
public abstract class ParallelComponentSystem extends AbstractComponentSystem implements IndividualUpdater {
    
    private final ExecutorService executorService;
    private final int size;
    private final int activationSize;
    
    public ParallelComponentSystem(final BitSet required, final int threads, final int activationSize) {
        super(required);
        this.size = threads;
        this.activationSize = activationSize;
        this.executorService = ExecutorsUtil.newFixedThreadPool(threads);
    }
    
    protected final ExecutorService getExecutor() {
        return this.executorService;
    }
    
    public int numThreads() {
        return this.size;
    }
    
    public int getActivationSize() {
        return this.activationSize;
    }
    
    @Override
    public final void update(final IECSManager entityManager, final Time time) {
        if (this.entities.size() > 0) {
            if (this.entities.size() < this.activationSize) {
                for (final Entity e : this.entities) {
                    updateIndividual(entityManager, e, time);
                }
            } else {
                updateThreaded(entityManager, this.entities, time);
            }
        }
    }
    
    public abstract void updateThreaded(IECSManager entityManager, List<Entity> entities, Time time);
    
}
