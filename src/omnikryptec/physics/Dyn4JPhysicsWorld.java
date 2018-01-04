package omnikryptec.physics;

import org.dyn4j.dynamics.World;

public class Dyn4JPhysicsWorld extends PhysicsWorld{

	private World world;
	
	public Dyn4JPhysicsWorld() {
		this.world = new World();
		world.setGravity(World.ZERO_GRAVITY);
	}
	
	public Dyn4JPhysicsWorld(World world) {
		this.world = world;
	}
	
	public World getWorld() {
		return world;
	}
	
	@Override
	protected int stepSimulation(float timeStep) {
		world.updatev(timeStep);
		return 0;
	}

	@Override
	protected int stepSimulation(float timeStep, int maxSubSteps) {
		world.updatev(timeStep);
		return 0;
	}

	@Override
	protected int stepSimulation(float timeStep, int maxSubSteps, float fixedTimeStep) {
		world.updatev(timeStep);
		return 0;
	}

	@Override
	protected float checkSimulationSpeed(float simulationSpeed) {
		return Math.max(simulationSpeed, 0.0F);
	}

}
