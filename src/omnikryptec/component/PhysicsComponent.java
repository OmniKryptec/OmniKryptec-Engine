package omnikryptec.component;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

import omnikryptec.entity.Entity;
import omnikryptec.entity.GameObject;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.main.Scene;
import omnikryptec.physics.RigidBodyBuilder;
import omnikryptec.renderer.RenderChunk;

/**
 * Component for physics
 * 
 * @author Panzer1119
 */
public class PhysicsComponent implements Component {

	private final GameObject instance;
	private final RigidBody body;
	private boolean pause = false;

	
	private RigidBodyBuilder rigidBodyBuilder;
	/**
	 * Constructs this Component with a standard RigidBodyBuilder
	 * 
	 * @param instance
	 *            GameObject Parent GameObject
	 * @param mass
	 *            Float Mass of the GameObject
	 */
	public PhysicsComponent(GameObject instance, float mass) {
		if (instance == null) {
			throw new NullPointerException("Instance must not be null!");
		}
		this.instance = instance;
		rigidBodyBuilder = new RigidBodyBuilder();
		if (instance instanceof Entity) {
			rigidBodyBuilder.loadFromEntity((Entity) instance);
		} else {
			rigidBodyBuilder.setDefaultMotionState(instance);
		}
		rigidBodyBuilder.setMass(mass);
		this.body = rigidBodyBuilder.create();
		init();
	}

	/**
	 * Constructs this component with a custom RigidBodyBuilder
	 * 
	 * @param instance
	 *            GameObject Parent GameObject
	 * @param rigidBodyBuilder
	 *            RigidBodyBuilder Custom RigidBodyBuilder
	 */
	public PhysicsComponent(GameObject instance, RigidBodyBuilder rigidBodyBuilder) {
		if (instance == null) {
			throw new NullPointerException("Instance must not be null!");
		} else if (rigidBodyBuilder == null) {
			throw new NullPointerException("RigidBodyBuilder must not be null!");
		}
		this.instance = instance;
		this.body = rigidBodyBuilder.create();
		init();
	}
	
	private RenderChunk chunk;
	
	private final void init() {
		if (instance != null) {
			chunk = instance.getMyChunk();
			if (chunk != null) {
				body.setActivationState(CollisionObject.DISABLE_DEACTIVATION); // FIXME
																				// ONLY
																				// FOR
																				// TESTING!!!
				manageRigidBodyStatus(null, chunk.getScene());
			} else if (Logger.isDebugMode()) {
				Logger.log("Chunk must not be null!", LogLevel.WARNING);
			}
		} else if (Logger.isDebugMode()) {
			Logger.log("Instance must not be null!", LogLevel.WARNING);
		}
	}

	private final void manageRigidBodyStatus(Scene oldScene, Scene newScene) {
		if (oldScene != null && oldScene.isUsingPhysics()) {
			oldScene.getPhysicsWorld().getWorld().removeRigidBody(body);
		}
		if (newScene != null && newScene.isUsingPhysics()) {
			newScene.getPhysicsWorld().getWorld().addRigidBody(body);
		}
	}

	/**
	 * Returns the parent GameObject
	 * 
	 * @return GameObject Parent GameObject
	 */
	public final GameObject getInstance() {
		return instance;
	}

	/**
	 * Returns the RigidBody
	 * 
	 * @return RigidBody RigidBody
	 */
	public final RigidBody getBody() {
		return body;
	}

	/**
	 * Returns if this component is paused
	 * 
	 * @return <tt>true</tt> if this component is paused
	 */
	public final boolean isPause() {
		return pause;
	}

	/**
	 * Sets if the component should be paused
	 * 
	 * @param pause
	 *            Boolean Sets if the component is paused
	 * @return PhysicsComponent A reference to this PhysicsComponent
	 */
	public final PhysicsComponent setPause(boolean pause) {
		this.pause = pause;
		return this;
	}
	
	private Vector3f ballPosition = new Vector3f();
	private Quat4f ballOrientation = new Quat4f();
	
	@Override
	public final void execute(GameObject instance) {
		if (pause || body == null) {
			return;
		}
		ballPosition = body.getMotionState().getWorldTransform(new Transform()).origin;
		instance.setRelativePos(ballPosition.x, ballPosition.y, ballPosition.z);
		ballOrientation = body.getOrientation(ballOrientation);
		instance.getRelativeRotation().set(ballOrientation.x, ballOrientation.y, ballOrientation.z);
	}

	@Override
	public final void onDelete(GameObject instance) {
		if (body != null) {
			manageRigidBodyStatus(instance.getMyChunk().getScene(), null);
			body.destroy();
		}
	}

	@Override
	public float getLevel() {
		return -1.0F;
	}

}
