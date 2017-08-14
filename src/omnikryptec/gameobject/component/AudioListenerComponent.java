package omnikryptec.gameobject.component;

import org.joml.Vector3f;

import omnikryptec.audio.AudioManager;
import omnikryptec.gameobject.GameObject;
import omnikryptec.main.Scene;
import omnikryptec.renderer.RenderChunk;
import omnikryptec.util.ConverterUtil;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

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
                Logger.log("AudioListenerComponent could not be set as the Main-AudioListenerComponent!", LogLevel.WARNING);
            } else {
                Logger.log("AudioListenerComponent was set as the Main-AudioListenerComponent", LogLevel.FINER);
            }
        }
    }

    private RenderChunk chunk;
    private Scene scene;
    private PhysicsComponent physicsComponent;
    private boolean isUsingPhysics = false;
    private Vector3f position, rotation;
    private javax.vecmath.Vector3f velocity = new javax.vecmath.Vector3f(0, 0, 0);

    ;

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
        position = instance.getTransform().getPosition(true);
        velocity.set(0, 0, 0);
        //TODO sind die eulerangles richtig herum?
        rotation = instance.getTransform().getRotation(true).getEulerAnglesXYZ(new Vector3f());
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
