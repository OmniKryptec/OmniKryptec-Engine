package de.omnikryptec.physics;

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
