package omnikryptec.physics;

import com.bulletphysics.dynamics.DynamicsWorld;

import omnikryptec.display.DisplayManager;

/**
 *
 * @author Panzer1119
 */
public class PhysicsWorld {

	private final DynamicsWorld dynamicsWorld;
	private float simulationSpeed = 1.0F;
	private boolean simulationPaused = false;

	public PhysicsWorld(DynamicsWorld dynamicsWorld) {
		this.dynamicsWorld = dynamicsWorld;
	}

	public final int stepSimulation() {
		int temp = -1;
		if (!simulationPaused && simulationSpeed > 0) {
			temp = dynamicsWorld.stepSimulation(getTimeStep());
		}
		return temp;
	}

	public final int stepSimulation(int maxSubSteps) {
		int temp = -1;
		if (!simulationPaused && simulationSpeed > 0) {
			temp = dynamicsWorld.stepSimulation(getTimeStep(), maxSubSteps);
		}
		return temp;
	}

	public final int stepSimulation(int maxSubSteps, float fixedTimeStep) {
		int temp = -1;
		if (!simulationPaused && simulationSpeed > 0) {
			temp = dynamicsWorld.stepSimulation(getTimeStep(), maxSubSteps, fixedTimeStep);
		}
		return temp;
	}

	private final float getTimeStep() {
		return DisplayManager.instance().getDeltaTimef() * simulationSpeed;
	}

	public final DynamicsWorld getWorld() {
		return dynamicsWorld;
	}

	public final float getSimulationSpeed() {
		return simulationSpeed;
	}

	public final PhysicsWorld setSimulationSpeed(float simulationSpeed) {
		if (simulationSpeed < 0) {
			simulationSpeed = 0;
		}
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
