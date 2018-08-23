package de.omnikryptec.physics;

import de.omnikryptec.graphics.GraphicsUtil;
import de.omnikryptec.settings.GameSettings;
import de.omnikryptec.util.Instance;
import org.dyn4j.dynamics.ContinuousDetectionMode;
import org.dyn4j.dynamics.World;

public class Dyn4JPhysicsWorld extends PhysicsWorld {

	private World world;
	private boolean aarb = false;
	private boolean updatevar = false;
	private int subs=5;
	
	
	public Dyn4JPhysicsWorld() {
		this.world = new World();
		world.setGravity(World.ZERO_GRAVITY);
		world.getSettings().setContinuousDetectionMode(ContinuousDetectionMode.BULLETS_ONLY);
		world.getSettings().setMaximumRotation(Double.POSITIVE_INFINITY);
		world.getSettings().setMaximumTranslation(Double.POSITIVE_INFINITY);
	
	}

	public Dyn4JPhysicsWorld(World world) {
		this.world = world;
	}

	public World getWorld() {
		return world;
	}

	@Override
	protected int stepSimulation(float timeStep) {
		update(timeStep);
		return 0;
	}

	@Override
	protected int stepSimulation(float timeStep, int maxSubSteps) {
		update(timeStep);
		return 0;
	}

	@Override
	protected int stepSimulation(float timeStep, int maxSubSteps, float fixedTimeStep) {
		update(timeStep);
		return 0;
	}

	private void update(float t) {
		if (aarb) {
			world.setUpdateRequired(true);
		}
		if (updatevar) {
			world.updatev(t);
		} else {
			//world.step(1, t);
			world.update(t,subs);
			//world.update(t, t, subs);
		}
		if (GraphicsUtil.needsUpdate(last, GameSettings.CHECKCHANGEFRAMES)) {
			last = Instance.getFramecount();
			aarb = Instance.getGameSettings().getBoolean(GameSettings.DYN4J_PHYSICS_REMOVE_ADD_LIFECYCLE);
			updatevar = Instance.getGameSettings().getBoolean(GameSettings.DYN4J_PHYSICS_VAR_TS);
			subs = Instance.getGameSettings().getInteger(GameSettings.DYN4J_MAX_SUBSTEPS);
		}
		if (aarb) {
			world.removeAllBodies();
		}
	}


	private long last = 0;

	@Override
	protected float checkSimulationSpeed(float simulationSpeed) {
		return Math.max(simulationSpeed, 0.0F);
	}

	public boolean raaBody() {
		return aarb;
	}

}
