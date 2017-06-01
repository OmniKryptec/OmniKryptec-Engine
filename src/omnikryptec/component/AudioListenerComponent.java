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
 *
 * @author Panzer1119
 */
public class AudioListenerComponent implements Component {
    
    public AudioListenerComponent() {
        boolean done = AudioManager.setBlockingComponent(this, this);
        if(Logger.isDebugMode()) {
            if(!done) {
                Logger.log("AudioListenerComponent could not be set as the Main-AudioListenerComponent!", LogLevel.WARNING);
            } else {
                Logger.log("AudioListenerComponent was set as the Main-AudioListenerComponent", LogLevel.FINER);
            }
        }
    }

    @Override
    public final void execute(GameObject instance) {
        boolean isUsingPhysics = false;
        RenderChunk chunk = instance.getMyChunk();
        Scene scene = null;
        PhysicsComponent physicsComponent = null;
        if(chunk != null) {
            scene = instance.getMyChunk().getScene();
            if(scene != null) {
                if(scene.isUsingPhysics()) {
                    physicsComponent = instance.getComponent(PhysicsComponent.class);
                    isUsingPhysics = ((physicsComponent != null) && (physicsComponent.getBody() != null));
                }
            }
        }
        final Vector3f position = instance.getAbsolutePos();
        final javax.vecmath.Vector3f velocity = new javax.vecmath.Vector3f(0, 0, 0);
        final Vector3f rotation = instance.getAbsoluteRotation();
        if(isUsingPhysics) {
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
        return 1.1F;
    }
    
}
