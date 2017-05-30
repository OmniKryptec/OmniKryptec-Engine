package omnikryptec.util;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

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

import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.model.Model;
import omnikryptec.objConverter.ModelData;

/**
 *
 * @author Panzer1119
 */
public class PhysicsUtil {
    
    public static final Vector3f X = new Vector3f(1, 0, 0);
    public static final Vector3f Y = new Vector3f(0, 1, 0);
    public static final Vector3f Z = new Vector3f(0, 0, 1);
    public static final Vector3f ZERO = new Vector3f(0, 0, 0);
    public static final Vector3f ONE = new Vector3f(1, 1, 1);

    public static final DynamicsWorld createDefaultDynamicsWorld() {
        final BroadphaseInterface broadphase = new DbvtBroadphase();
        final CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        final CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        final ConstraintSolver solver = new SequentialImpulseConstraintSolver();
        final DynamicsWorld dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        dynamicsWorld.setGravity(Constants.GRAVITY_EARTH);
        return dynamicsWorld;
    }
    
    public static final MotionState createDefaultMotionStateOfPosition(org.lwjgl.util.vector.Vector3f position, org.lwjgl.util.vector.Vector3f rotation) {
        return createDefaultMotionStateOfPosition(ConverterUtil.convertVector3fFromLWJGL(position), ConverterUtil.convertVector3fFromLWJGL(rotation));
    }
    
    public static final MotionState createDefaultMotionStateOfPosition(Vector3f position, Vector3f rotation) {
        return new DefaultMotionState(createTransform(position, rotation));
    }
    
    public static final Transform createTransform(Vector3f position, Vector3f rotation) {
        return new Transform(new Matrix4f(new Quat4f(rotation.x, rotation.y, rotation.z, 1), position, 1));
    }
    
    public static final CollisionShape createConvexHullShape(Model model) {
        if(model == null) {
            if(Logger.isDebugMode()) {
                Logger.log("Cannot create ConvexHullShape of a null Model! Returning default CollisionShape!", LogLevel.WARNING);
            }
            return createStandardCollisionShape(1.0F);
        }
        return createConvexHullShape(model.getModelData());
    }
    
    public static final CollisionShape createConvexHullShape(ModelData modelData) {
        if(modelData == null) {
            if(Logger.isDebugMode()) {
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
        if(vertices == null) {
            if(Logger.isDebugMode()) {
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
