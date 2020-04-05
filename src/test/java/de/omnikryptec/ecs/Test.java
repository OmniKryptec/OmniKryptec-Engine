/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.ecs;

import de.omnikryptec.ecs.impl.ECSManager;
import de.omnikryptec.util.ExecutorsUtil;
import de.omnikryptec.util.updater.Time;

public class Test {

    public static void main(final String[] args) {
        System.out.println("Started...");
        final IECSManager manager = new ECSManager();
        final DoSomethingSystem system = new DoSomethingSystem();
        manager.addSystem(system);
        // manager.addSystem(new AnotherSystem());
        // manager.addSystem(new SomeOtherSystem());
        final int updt = 100;
        final int ents = 1_000_000;
        System.out.println("Testing with " + ents + " entities and " + updt + " updates");
        long time = System.currentTimeMillis();
        for (int i = 0; i < ents; i++) {
            manager.addEntity(new Entity().addComponent(new SomeDataComponent()));
        }
        long time2 = System.currentTimeMillis();
        System.out.println("Initialization took " + (time2 - time) * 1000 + " micro-s");
        System.out.println("Per Entity: " + (time2 - time) / (double) ents * 1000 + " micro-s");
        System.out.println("Starting updates...");
        time = System.currentTimeMillis();
        for (int i = 0; i < updt; i++) {
            manager.update(new Time(i, 0, 0, 1));
        }
        time2 = System.currentTimeMillis() - time;
        System.out.println("Time per update: " + time2 * 1000 / (double) updt + " micro-s");
        System.out.println("Time per entity: " + time2 * 1000 / (double) (updt * ents) + " micro-s");
        manager.removeSystem(system);
        ExecutorsUtil.shutdownNowAll();
    }

}
