package omnikryptec.test;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import java.util.HashMap;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import omnikryptec.camera.Camera;
import omnikryptec.display.DisplayManager;
import omnikryptec.settings.GameSettings;
import omnikryptec.event.EventSystem;
import omnikryptec.event.EventType;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene;
import omnikryptec.objConverter.ObjLoader;
import omnikryptec.storing.Entity;
import omnikryptec.storing.EntityBuilder;
import omnikryptec.storing.Model;
import omnikryptec.storing.TexturedModel;
import omnikryptec.texture.Texture;
import omnikryptec.util.InputUtil;
import omnikryptec.util.NativesLoader;
import org.lwjgl.input.Keyboard;

/**
 *
 * @author Panzer1119
 */
public class JBulletTest {
    
    private static final Transform DEFAULT_BALL_TRANSFORM = new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, 35, 0), 1.0F));
    
    private static DynamicsWorld dynamicsWorld;
    private static final HashMap<Entity, RigidBody> balls = new HashMap<>();
    private static Entity controlBallEntity;
    private static RigidBody controlBall;
    private static boolean applyForce = false;
    private static boolean createNewShape = false;
    private static boolean resetControlBall = false;
    
    private static void setUpPhysics() {
        try {
            final BroadphaseInterface broadphase = new DbvtBroadphase();
            final CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
            final CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
            final ConstraintSolver solver = new SequentialImpulseConstraintSolver();
            dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
            dynamicsWorld.setGravity(new Vector3f(0, -10F, 0)); //-9.81F m/s^2
            final CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0, 1, 0), 0.25F); //m
            final CollisionShape ballShape = new SphereShape(3.0F); //m
            final MotionState groundMotionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, 0, 0), 1.0F)));
            final RigidBodyConstructionInfo goundBodyConstructionInfo = new RigidBodyConstructionInfo(0, groundMotionState, groundShape, new Vector3f(0, 0, 0)); //Vector3f unnoetig!
            goundBodyConstructionInfo.restitution = 0.25F;
            final RigidBody groundRigidBody = new RigidBody(goundBodyConstructionInfo);
            dynamicsWorld.addRigidBody(groundRigidBody);
            final MotionState ballMotionState = new DefaultMotionState(DEFAULT_BALL_TRANSFORM);
            final Vector3f ballInertia = new Vector3f(0, 0, 0);
            ballShape.calculateLocalInertia(2.5F, ballInertia);
            final RigidBodyConstructionInfo ballConstructionInfo = new RigidBodyConstructionInfo(2.5F, ballMotionState, ballShape, ballInertia);
            ballConstructionInfo.restitution = 0.5F;
            ballConstructionInfo.angularDamping = 0.95F;
            controlBall = new RigidBody(ballConstructionInfo);
            controlBall.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
            dynamicsWorld.addRigidBody(controlBall);
            controlBallEntity = createBallEntity(controlBall);
            balls.put(controlBallEntity, controlBall);
            OmniKryptecEngine.instance().getCurrentScene().addGameObject(controlBallEntity);
            
        } catch (Exception ex) {
            Logger.logErr("Error while setting up physics: " + ex, ex);
        }
    }
    
    private static void logic() {
        dynamicsWorld.stepSimulation(1 / 250.0F/*DisplayManager.instance().getFPS()*/);
        /*
        final HashMap<Entity, RigidBody> ballsToBeRemoved = new HashMap<>();
        for(Entity e : balls.keySet()) {
            final RigidBody body = balls.get(e);
            final Vector3f position = body.getMotionState().getWorldTransform(new Transform()).origin;
            if(!body.equals(controlBall) && (position.x < -50 || position.x > 50 || position.z < -50 || position.z > 50)) {
                ballsToBeRemoved.put(e, body);
            }
        }
        for(Entity e : ballsToBeRemoved.keySet()) {
            final RigidBody body = ballsToBeRemoved.get(e);
            OmniKryptecEngine.instance().getCurrentScene().removeGameObject(e);
            balls.remove(e);
            dynamicsWorld.removeRigidBody(body);
        }
        */
        if(applyForce) {
            final Transform controlBallTransform = new Transform();
            controlBall.getMotionState().getWorldTransform(controlBallTransform);
            final Vector3f controlBallLocation = controlBallTransform.origin;
            final Vector3f cameraPosition = convertVector3fFromLWJGL(OmniKryptecEngine.getInstance().getCurrentScene().getCamera().getRelativePos());
            final Vector3f force = new Vector3f();
            force.sub(cameraPosition, controlBallLocation);
            controlBall.activate(true);
            controlBall.applyCentralForce(force);
        }
        if(createNewShape) {
            final CollisionShape shape = new SphereShape(3.0F); //m
            final DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(OmniKryptecEngine.instance().getCurrentScene().getCamera().getRelativePos().x, 35F, OmniKryptecEngine.instance().getCurrentScene().getCamera().getRelativePos().z), 1)));
            final Vector3f inertia = new Vector3f();
            shape.calculateLocalInertia(1.0F, inertia);
            final RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(1.0F, motionState, shape, inertia);
            constructionInfo.restitution = 0.75F;
            final RigidBody body = new RigidBody(constructionInfo);
            dynamicsWorld.addRigidBody(body);
            final Entity entity = createBallEntity(body);
            balls.put(entity, body);
            OmniKryptecEngine.instance().getCurrentScene().addGameObject(entity);
            createNewShape = false;
        }
        if(resetControlBall) {
            controlBall.setCenterOfMassTransform(DEFAULT_BALL_TRANSFORM);
            controlBall.setAngularVelocity(new Vector3f(0, 0, 0));
            controlBall.setLinearVelocity(new Vector3f(0, 0, 0));
            resetControlBall = false;
        }
    }
    
    private static void input() {
        applyForce = InputUtil.isKeyDown(Keyboard.KEY_F);
        createNewShape = InputUtil.isKeyDown(Keyboard.KEY_N);
        resetControlBall = InputUtil.isKeyDown(Keyboard.KEY_R);
    }
    
    private static Entity createBallEntity(RigidBody body) {
        return new Entity(entityBuilder.createTexturedModel()) {
            
            @Override
            public void doLogic() {
                final Vector3f ballPosition = body.getMotionState().getWorldTransform(new Transform()).origin;
                setRelativePos(ballPosition.x, ballPosition.y, ballPosition.z);
                final Quat4f ballOrientation = body.getOrientation(new Quat4f());
                setRotation(new org.lwjgl.util.vector.Vector3f(ballOrientation.x, ballOrientation.y, ballOrientation.z));
            }
            
        };
    }
    
    private static org.lwjgl.util.vector.Vector3f convertVector3fToLWJGL(Vector3f vector) {
        return new org.lwjgl.util.vector.Vector3f(vector.x, vector.y, vector.z);
    }
    
    private static Vector3f convertVector3fFromLWJGL(org.lwjgl.util.vector.Vector3f vector) {
        return new Vector3f(vector.x, vector.y, vector.z);
    }
    
    private static EntityBuilder entityBuilder;
    
    public static void main(String[] args) {
        try {
            NativesLoader.loadNatives();
            OmniKryptecEngine.addShutdownHook(() -> NativesLoader.unloadNatives());
            Logger.enableLoggerRedirection(true);
            Logger.setDebugMode(true);
            Logger.CONSOLE.setExitWhenLastOne(true);
            Logger.showConsoleDirect();
            
            DisplayManager.createDisplay("JBullet Test", new GameSettings("JBulletTest", 1280, 720).setAnisotropicLevel(32).setMultisamples(32));
            OmniKryptecEngine.instance().addAndSetScene("TestScene", new Scene(new Camera() {
                
                @Override
                public void doLogic() {
                    final float deltaTime = DisplayManager.instance().getDeltaTime();
                    InputUtil.doCameraLogic(this, DisplayManager.instance().getSettings().getKeySettings(), 1.5F * deltaTime, 15.0F * deltaTime);
                }
                
            }.setPerspectiveProjection(75, 1000, 0.1F)));
            entityBuilder = new EntityBuilder("/omnikryptec/test/brunnen.obj", "/omnikryptec/test/brunnen.png");
            final Entity entity_1 = entityBuilder.create();
            OmniKryptecEngine.instance().getCurrentScene().addGameObject(entity_1);
            OmniKryptecEngine.instance().getCurrentScene().getCamera().getRelativePos().y += 3;
            OmniKryptecEngine.instance().getCurrentScene().getCamera().getRelativeRotation().x = 40;
            entity_1.setRelativePos(0, 0, -5);
            EventSystem.instance().addEventHandler(e -> {input(); logic();}, EventType.RENDER_EVENT);
            /*OmniKryptecEngine.instance().getCurrentScene().addGameObject(new Entity(brunnen_tm) {
                
                @Override
                public void doLogic() {
                    render();
                }
                
            });*/
            setUpPhysics();
            OmniKryptecEngine.instance().startLoop(OmniKryptecEngine.ShutdownOption.JAVA);
        } catch (Exception ex) {
            Logger.logErr("Main Error: " + ex, ex);
        }
    }
    
}