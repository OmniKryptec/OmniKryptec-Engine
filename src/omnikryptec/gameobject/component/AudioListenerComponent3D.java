package omnikryptec.gameobject.component;

import org.joml.Vector3f;

import omnikryptec.audio.AudioManager;
import omnikryptec.gameobject.GameObject3D;
import omnikryptec.main.AbstractScene3D;
import omnikryptec.renderer.d3.RenderChunk3D;
import omnikryptec.util.ConverterUtil;
import omnikryptec.util.Priority;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

/**
 * Component for listening the sounds
 *
 * @author Panzer1119
 */
@Priority( value = 2f )
@ComponentAnnotation(supportedGameObjectClass = GameObject3D.class)
public class AudioListenerComponent3D extends Component<GameObject3D> {

    /**
     * Normal constructor
     */
    public AudioListenerComponent3D() {
        boolean done = AudioManager.setBlockingComponent(this, this);
        if (Logger.isDebugMode()) {
            if (!done) {
                Logger.log("AudioListenerComponent could not be set as the Main-AudioListenerComponent!", LogLevel.WARNING);
            } else {
                Logger.log("AudioListenerComponent was set as the Main-AudioListenerComponent", LogLevel.FINER);
            }
        }
    }

    private RenderChunk3D chunk;
    private AbstractScene3D scene;
    private PhysicsComponent3D physicsComponent;
    private boolean isUsingPhysics = false;
    private Vector3f position, rotation;
    private javax.vecmath.Vector3f velocity = new javax.vecmath.Vector3f(0, 0, 0);

    ;

	@Override
    public final void execute(GameObject3D instance) {
		isUsingPhysics = false;
        chunk = instance.getRenderChunk();
        physicsComponent = null;
        if (chunk != null) {
            scene = chunk.getScene();
            if (scene != null) {
                if (scene.isUsingPhysics()) {
                    physicsComponent = instance.getComponent(PhysicsComponent3D.class);
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
    public final void onDelete(GameObject3D instance) {
        AudioManager.setBlockingComponent(this, null);
    }

}
