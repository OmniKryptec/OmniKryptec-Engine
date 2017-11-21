package omnikryptec.physics;

import omnikryptec.main.OmniKryptecEngine;

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

    public final float getTimeStep() {
        return OmniKryptecEngine.instance().getDisplayManager().getDeltaTimef() * simulationSpeed;
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
