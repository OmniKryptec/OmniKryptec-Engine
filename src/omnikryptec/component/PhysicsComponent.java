package omnikryptec.component;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import omnikryptec.entity.Entity;
import omnikryptec.entity.GameObject;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.main.Scene;
import omnikryptec.physics.RigidBodyBuilder;
import omnikryptec.renderer.RenderChunk;

/**
 *
 * @author Panzer1119
 */
public class PhysicsComponent implements Component {
    
    private final GameObject instance;
    private final RigidBody body;
    private boolean pause = false;
    
    public PhysicsComponent(GameObject instance, float mass) {
        if(instance == null) {
            throw new NullPointerException("Instance must not be null!");
        }
        this.instance = instance;
        RigidBodyBuilder rigidBodyBuilder = new RigidBodyBuilder();
        if(instance instanceof Entity) {
            rigidBodyBuilder.loadFromEntity((Entity) instance);
        } else {
            rigidBodyBuilder.setDefaultMotionState(instance);
        }
        rigidBodyBuilder.setMass(mass);
        this.body = rigidBodyBuilder.create();
        init();
    }
    
    public PhysicsComponent(GameObject instance, RigidBodyBuilder rigidBodyBuilder) {
        if(instance == null) {
            throw new NullPointerException("Instance must not be null!");
        } else if(rigidBodyBuilder == null) {
            throw new NullPointerException("RigidBodyBuilder must not be null!");
        }
        this.instance = instance;
        this.body = rigidBodyBuilder.create();
        init();
    }
    
    private final void init() {
        if(instance != null) {
            RenderChunk chunk = instance.getMyChunk();
            if(chunk != null) {
                manageRigidBodyStatus(null, chunk.getScene());
            } else if(Logger.isDebugMode()) {
                Logger.log("Chunk must not be null!", LogLevel.WARNING);
            }
        } else if(Logger.isDebugMode()) {
            Logger.log("Instance must not be null!", LogLevel.WARNING);
        }
    }
    
    private final void manageRigidBodyStatus(Scene oldScene, Scene newScene) {
        if(oldScene != null && oldScene.isUsingPhysics()) {
            oldScene.getPhysicsWorld().getWorld().removeRigidBody(body);
        }
        if(newScene != null && newScene.isUsingPhysics()) {
            newScene.getPhysicsWorld().getWorld().addRigidBody(body);
        }
    }

    public final GameObject getInstance() {
        return instance;
    }

    public final RigidBody getBody() {
        return body;
    }

    public final boolean isPause() {
        return pause;
    }

    public final PhysicsComponent setPause(boolean pause) {
        this.pause = pause;
        return this;
    }
    
    @Override
    public final void execute(GameObject instance) {
        final Vector3f ballPosition = body.getMotionState().getWorldTransform(new Transform()).origin;
        instance.setRelativePos(ballPosition.x, ballPosition.y, ballPosition.z);
        final Quat4f ballOrientation = body.getOrientation(new Quat4f());
        instance.setRotation(new org.lwjgl.util.vector.Vector3f(ballOrientation.x, ballOrientation.y, ballOrientation.z));
    }

    @Override
    public final void onDelete(GameObject instance) {
        if(body != null) {
            manageRigidBodyStatus(instance.getMyChunk().getScene(), null);
            body.destroy();
        }
    }
    
}
