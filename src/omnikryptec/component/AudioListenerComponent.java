package omnikryptec.component;

import org.lwjgl.util.vector.Vector3f;

import omnikryptec.audio.AudioManager;
import omnikryptec.entity.GameObject;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.main.Scene;
import omnikryptec.renderer.RenderChunk;
import omnikryptec.util.ConverterUtil;

/**
 * Component for listening the sounds
 * 
 * @author Panzer1119
 */
public class AudioListenerComponent implements Component {

	/**
	 * Normal constructor
	 */
	public AudioListenerComponent() {
		boolean done = AudioManager.setBlockingComponent(this, this);
		if (Logger.isDebugMode()) {
			if (!done) {
				Logger.log("AudioListenerComponent could not be set as the Main-AudioListenerComponent!",
						LogLevel.WARNING);
			} else {
				Logger.log("AudioListenerComponent was set as the Main-AudioListenerComponent", LogLevel.FINER);
			}
		}
	}
	
	private RenderChunk chunk;
	private	Scene scene;
	private PhysicsComponent physicsComponent;
	private boolean isUsingPhysics = false;
	private Vector3f position, rotation;
	private javax.vecmath.Vector3f velocity = new javax.vecmath.Vector3f(0, 0, 0);;
	
	@Override
	public final void execute(GameObject instance) {
		isUsingPhysics = false;
		chunk = instance.getMyChunk();
		physicsComponent = null;
		if (chunk != null) {
			scene = chunk.getScene();
			if (scene != null) {
				if (scene.isUsingPhysics()) {
					physicsComponent = instance.getComponent(PhysicsComponent.class);
					isUsingPhysics = ((physicsComponent != null) && (physicsComponent.getBody() != null));
				}
			}
		}
		position = instance.getAbsolutePos();
		velocity.set(0,0,0);
		rotation = instance.getAbsoluteRotation();
		if (isUsingPhysics) {
			physicsComponent.getBody().getAngularVelocity(velocity);
		}
		AudioManager.setListenerData(this, position, rotation, ConverterUtil.convertVector3fToLWJGL(velocity));
	}

	@Override
	public final void onDelete(GameObject instance) {
		AudioManager.setBlockingComponent(this, null);
	}

	@Override
	public float getLevel() {
		return 2F;
	}

}
