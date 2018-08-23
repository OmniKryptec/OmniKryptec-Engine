/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.gameobject.component;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import de.omnikryptec.gameobject.Entity;
import de.omnikryptec.gameobject.GameObject;
import de.omnikryptec.gameobject.GameObject3D;
import de.omnikryptec.main.AbstractScene3D;
import de.omnikryptec.physics.JBulletPhysicsWorld;
import de.omnikryptec.physics.RigidBodyBuilder;
import de.omnikryptec.renderer.d3.RenderChunk3D;
import de.omnikryptec.util.Priority;
import de.omnikryptec.util.logger.LogLevel;
import de.omnikryptec.util.logger.Logger;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * Component for physics
 *
 * @author Panzer1119
 */
@Priority( value = -1f)
@ComponentAnnotation(supportedGameObjectClass = GameObject3D.class)
public class PhysicsComponent3D extends Component<GameObject3D> {

    private final GameObject3D instance;
    private final RigidBody body;
    private boolean pause = false;

    private RigidBodyBuilder rigidBodyBuilder;

    /**
     * Constructs this Component with a standard RigidBodyBuilder
     *
     * @param instance GameObject Parent GameObject
     * @param mass Float Mass of the GameObject
     */
    public PhysicsComponent3D(GameObject3D instance, float mass) {
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
     * @param instance GameObject Parent GameObject
     * @param rigidBodyBuilder RigidBodyBuilder Custom RigidBodyBuilder
     */
    public PhysicsComponent3D(GameObject3D instance, RigidBodyBuilder rigidBodyBuilder) {
        if (instance == null) {
            throw new NullPointerException("Instance must not be null!");
        } else if (rigidBodyBuilder == null) {
            throw new NullPointerException("RigidBodyBuilder must not be null!");
        }
        this.instance = instance;
        this.body = rigidBodyBuilder.create();
        init();
    }

    private RenderChunk3D chunk;

    private final void init() {
        if (instance != null) {
            chunk = instance.getRenderChunk();
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

    private final void manageRigidBodyStatus(AbstractScene3D oldScene, AbstractScene3D newScene) {
        if (oldScene != null && oldScene.isUsingPhysics() && oldScene.getPhysicsWorld() instanceof JBulletPhysicsWorld) {
            ((JBulletPhysicsWorld) oldScene.getPhysicsWorld()).getWorld().removeRigidBody(body);
        }
        if (newScene != null && newScene.isUsingPhysics() && newScene.getPhysicsWorld() instanceof JBulletPhysicsWorld) {
            ((JBulletPhysicsWorld) newScene.getPhysicsWorld()).getWorld().addRigidBody(body);
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
     * @param pause Boolean Sets if the component is paused
     * @return PhysicsComponent A reference to this PhysicsComponent
     */
    public final PhysicsComponent3D setPause(boolean pause) {
        this.pause = pause;
        return this;
    }

    private Vector3f ballPosition = new Vector3f();
    private Quat4f ballOrientation = new Quat4f();

    @Override
    public final void execute(GameObject3D instance) {
        if (pause || body == null) {
            return;
        }
        ballPosition = body.getMotionState().getWorldTransform(new Transform()).origin;
        instance.getTransform().setPosition(ballPosition.x, ballPosition.y, ballPosition.z);
        ballOrientation = body.getOrientation(ballOrientation);
        instance.getTransform().setRotation(ballOrientation.x, ballOrientation.y, ballOrientation.z, ballOrientation.w);
    }

    @Override
    public final void onDelete(GameObject3D instance) {
        if (body != null) {
            manageRigidBodyStatus(instance.getRenderChunk().getScene(), null);
            body.destroy();
        }
    }

}
