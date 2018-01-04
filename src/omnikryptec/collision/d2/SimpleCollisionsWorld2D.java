package omnikryptec.collision.d2;

import omnikryptec.physics.PhysicsWorld;

public class SimpleCollisionsWorld2D extends PhysicsWorld{
	
	private CollisionManager manager = new CollisionManager();
	
	@Override
	protected int stepSimulation(float timeStep) {
		manager.step();
		return 0;
	}

	@Override
	protected int stepSimulation(float timeStep, int maxSubSteps) {
		manager.step();
		return 0;
	}

	@Override
	protected int stepSimulation(float timeStep, int maxSubSteps, float fixedTimeStep) {
		manager.step();
		return 0;
	}

	@Override
	protected float checkSimulationSpeed(float simulationSpeed) {
		return simulationSpeed;
	}
	
	@Override
	public void preLogic() {
		manager.prepareStep();
	}

	
	public void addRectangle(Rectangle r) {
		manager.add(r);
	}
	
}
