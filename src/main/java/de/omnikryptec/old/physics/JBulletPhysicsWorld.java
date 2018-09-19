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

package de.omnikryptec.old.physics;

import com.bulletphysics.dynamics.DynamicsWorld;

/**
 * JBulletPhysicsWorld
 *
 * @author Panzer1119
 */
public class JBulletPhysicsWorld extends PhysicsWorld {

    private final DynamicsWorld dynamicsWorld;

    public JBulletPhysicsWorld(DynamicsWorld dynamicsWorld) {
        this.dynamicsWorld = dynamicsWorld;
    }

    public final DynamicsWorld getWorld() {
        return dynamicsWorld;
    }

    @Override
    protected final int stepSimulation(float timeStep) {
        return dynamicsWorld.stepSimulation(timeStep);
    }

    @Override
    protected final int stepSimulation(float timeStep, int maxSubSteps) {
        return dynamicsWorld.stepSimulation(timeStep, maxSubSteps);
    }

    @Override
    protected final int stepSimulation(float timeStep, int maxSubSteps, float fixedTimeStep) {
        return dynamicsWorld.stepSimulation(timeStep, maxSubSteps, fixedTimeStep);
    }

    @Override
    protected float checkSimulationSpeed(float simulationSpeed) {
        return Math.max(simulationSpeed, 0.0F);
    }

}
