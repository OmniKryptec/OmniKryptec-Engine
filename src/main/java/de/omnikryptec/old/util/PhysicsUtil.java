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

package de.omnikryptec.old.util;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;
import de.omnikryptec.old.resource.model.Model;
import de.omnikryptec.old.resource.objConverter.ModelData;
import de.omnikryptec.old.util.logger.LogLevel;
import de.omnikryptec.old.util.logger.Logger;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * Physics utility class
 *
 * @author Panzer1119
 */
public class PhysicsUtil {
    
    public static final Vector3f X = new Vector3f(1, 0, 0);
    public static final Vector3f Y = new Vector3f(0, 1, 0);
    public static final Vector3f Z = new Vector3f(0, 0, 1);
    public static final Vector3f ZERO = new Vector3f(0, 0, 0);
    public static final Vector3f ONE = new Vector3f(1, 1, 1);
    public static final float GRAVITATIONAL_CONSTANT = 6.67408E-11F;
    
    public static final DynamicsWorld createDefaultDynamicsWorld() {
        final BroadphaseInterface broadphase = new DbvtBroadphase();
        final CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        final CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        final ConstraintSolver solver = new SequentialImpulseConstraintSolver();
        final DynamicsWorld dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        dynamicsWorld.setGravity(Instance.GRAVITY_EARTH);
        return dynamicsWorld;
    }
    
    public static final MotionState createDefaultMotionStateOfPosition(org.joml.Vector3f position, org.joml.Quaternionf rotation) {
        return createDefaultMotionStateOfPosition(ConverterUtil.convertVector3fFromLWJGL(position), ConverterUtil.convertQuat4fFromLWJGL(rotation));
    }
    
    public static final MotionState createDefaultMotionStateOfPosition(Vector3f position, Quat4f rotation) {
        return new DefaultMotionState(createTransform(position, rotation));
    }
    
    public static final Transform createTransform(Vector3f position, Quat4f rotation) {
        return new Transform(new Matrix4f(rotation, position, 1));
    }
    
    public static final CollisionShape createConvexHullShape(Model model) {
        if (model == null) {
            if (Logger.isDebugMode()) {
                Logger.log("Cannot create ConvexHullShape of a null Model! Returning default CollisionShape!", LogLevel.WARNING);
            }
            return createStandardCollisionShape(1.0F);
        }
        return createConvexHullShape(model.getModelData());
    }
    
    public static final CollisionShape createConvexHullShape(ModelData modelData) {
        if (modelData == null) {
            if (Logger.isDebugMode()) {
                Logger.log("Cannot create ConvexHullShape of a null ModelData! Returning default CollisionShape!", LogLevel.WARNING);
            }
            return createStandardCollisionShape(1.0F);
        }
        return createConvexHullShape(modelData.getVertices());
    }
    
    public static final CollisionShape createConvexHullShape(float[] vertices) {
        return createConvexHullShape(ConverterUtil.convertToObjectArrayListVector3f(vertices));
    }
    
    public static final CollisionShape createConvexHullShape(ObjectArrayList<Vector3f> vertices) {
        if (vertices == null) {
            if (Logger.isDebugMode()) {
                Logger.log("Cannot create ConvexHullShape of a null ObjectArrayList! Returning default CollisionShape!", LogLevel.WARNING);
            }
            return createStandardCollisionShape(1.0F);
        }
        return new ConvexHullShape(vertices);
    }
    
    public static final CollisionShape createStandardCollisionShape(float radius) {
        return new SphereShape(radius);
    }
    
}
