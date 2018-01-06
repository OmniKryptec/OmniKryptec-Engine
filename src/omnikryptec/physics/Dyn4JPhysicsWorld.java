package omnikryptec.physics;

import org.dyn4j.UnitConversion;
import org.dyn4j.dynamics.World;

import omnikryptec.graphics.GraphicsUtil;
import omnikryptec.settings.GameSettings;
import omnikryptec.util.Instance;

public class Dyn4JPhysicsWorld extends PhysicsWorld {

	private World world;
	private boolean aarb = false;
	private boolean updatevar = false;
	private int subs=5;
	
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
			world.update(t,subs);
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

	@Override
	public void preLogic() {

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
