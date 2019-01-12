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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.util.ExecutorsUtil;
import de.omnikryptec.util.math.Mathd;
import de.omnikryptec.util.updater.Time;

public abstract class GroupParallelComponentSystem extends ParallelComponentSystem {
    
    public GroupParallelComponentSystem(final BitSet required) {
        this(required, ExecutorsUtil.AVAILABLE_PROCESSORS, ExecutorsUtil.AVAILABLE_PROCESSORS * 3);
    }
    
    public GroupParallelComponentSystem(final BitSet required, final int threads, final int activationSize) {
        super(required, threads, activationSize);
    }
    
    @Override
    public void updateThreaded(final IECSManager entityManager, final List<Entity> entities, final Time time) {
        final List<List<Entity>> lists = Lists.partition(entities,
                (int) Mathd.ceil(entities.size() / (double) numThreads()));
        final Collection<Callable<Void>> tasks = new ArrayList<>(numThreads());
        for (final List<Entity> el : lists) {
            tasks.add(() -> {
                for (final Entity e : el) {
                    updateIndividual(entityManager, e, time);
                }
                return null;
            });
        }
        try {
            getExecutor().invokeAll(tasks, 1, TimeUnit.MINUTES);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
}
