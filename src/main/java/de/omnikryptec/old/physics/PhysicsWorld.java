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

import de.omnikryptec.old.main.OmniKryptecEngine;

/**
 * PhysicsWorld
 *
 * @author Panzer1119
 */
public abstract class PhysicsWorld {

    private float simulationSpeed = 1.0F;
    private boolean simulationPaused = false;

    
    public final int stepSimulation() {
        int temp = -1;
        if (!simulationPaused && simulationSpeed > 0) {
            temp = stepSimulation(getTimeStep());
        }
        return temp;
    }

    public final int stepSimulation(int maxSubSteps) {
        int temp = -1;
        if (!simulationPaused && simulationSpeed > 0) {
            temp = stepSimulation(getTimeStep(), maxSubSteps);
        }
        return temp;
    }

    public final int stepSimulation(int maxSubSteps, float fixedTimeStep) {
        int temp = -1;
        if (!simulationPaused && simulationSpeed > 0) {
            temp = stepSimulation(getTimeStep(), maxSubSteps, fixedTimeStep);
        }
        return temp;
    }

    protected abstract int stepSimulation(float timeStep);

    protected abstract int stepSimulation(float timeStep, int maxSubSteps);

    protected abstract int stepSimulation(float timeStep, int maxSubSteps, float fixedTimeStep);
    
    protected abstract float checkSimulationSpeed(float simulationSpeed);

    public void preLogic() {

    }
    
    public final float getTimeStep() {
        return OmniKryptecEngine.instance().getDeltaTimeSf() * simulationSpeed;
    }

    public final float getSimulationSpeed() {
        return simulationSpeed;
    }

    public final PhysicsWorld setSimulationSpeed(float simulationSpeed) {
        simulationSpeed = checkSimulationSpeed(simulationSpeed);
        this.simulationSpeed = simulationSpeed;
        return this;
    }

    public final boolean isSimulationPaused() {
        return simulationPaused;
    }

    public final PhysicsWorld setSimulationPaused(boolean simulationPaused) {
        this.simulationPaused = simulationPaused;
        return this;
    }

    public final PhysicsWorld resetSpeed() {
        this.simulationSpeed = 1.0F;
        return this;
    }
	

}
