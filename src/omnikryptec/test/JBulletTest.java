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

import omnikryptec.display.DisplayManager;
import omnikryptec.entity.Camera;
import omnikryptec.entity.Entity;
import omnikryptec.entity.EntityBuilder;
import omnikryptec.entity.FollowingCamera;
import omnikryptec.entity.GameObject;
import omnikryptec.settings.GameSettings;
import omnikryptec.event.EventSystem;
import omnikryptec.event.EventType;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene;
import omnikryptec.util.ConverterUtil;

import omnikryptec.util.InputUtil;
import omnikryptec.util.NativesLoader;
import omnikryptec.util.PhysicsUtil;
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
            controlBallEntity = createBallEntity(entityBuilder_brunnen, controlBall);
            balls.put(controlBallEntity, controlBall);
            OmniKryptecEngine.instance().getCurrentScene().addGameObject(controlBallEntity);
        } catch (Exception ex) {
            Logger.logErr("Error while setting up physics: " + ex, ex);
        }
    }
    
    private static boolean physicsPause = false;
    private static float physicsSpeed = 1.0F;
    
    private static void logic() {
        if(!physicsPause && physicsSpeed > 0) {
            dynamicsWorld.stepSimulation((1.0F / DisplayManager.instance().getFPS()) * physicsSpeed);
        }
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
        if(applyForce) {
            final Camera camera = OmniKryptecEngine.instance().getCurrentScene().getCamera();
            //applyForceToBall(camera, controlBall);
            for(RigidBody body : balls.values()) {
                applyForceToBall(camera, body);
            }
        }
        if(createNewShape) {
            createNewShape(entityBuilder_brunnen);
            createNewShape = false;
        }
        if(resetControlBall) {
            controlBall.setCenterOfMassTransform(DEFAULT_BALL_TRANSFORM);
            controlBall.setAngularVelocity(new Vector3f(0, 0, 0));
            controlBall.setLinearVelocity(new Vector3f(0, 0, 0));
            resetControlBall = false;
        }
    }
    
    private static void applyForceToBall(Camera camera, RigidBody ball) {
        final Transform ballTransform = new Transform();
        ball.getMotionState().getWorldTransform(ballTransform);
        final Vector3f ballLocation = ballTransform.origin;
        final Vector3f cameraPosition = ConverterUtil.convertVector3fFromLWJGL((camera instanceof FollowingCamera) ? ((FollowingCamera) camera).getFollowedGameObject().getAbsolutePos() : camera.getAbsolutePos());
        final Vector3f force = new Vector3f();
        force.sub(cameraPosition, ballLocation);
        ball.activate(true);
        ball.applyCentralForce(force);
    }
    
    private static RigidBody createNewRigidBody(EntityBuilder entityBuilder) {
        //final CollisionShape shape = new SphereShape(entityBuilder.getModel().getRadius() / 10); //Standard 3.0F //m
        GameObject relateTo = OmniKryptecEngine.instance().getCurrentScene().getCamera();
        if(relateTo instanceof FollowingCamera) {
            GameObject temp = ((FollowingCamera) relateTo).getFollowedGameObject();
            if(temp != null) {
                relateTo = temp;
            }
        }
        final Vector3f position = new Vector3f(relateTo.getAbsolutePos().x, 35F, relateTo.getAbsolutePos().z);
        final CollisionShape shape = PhysicsUtil.createConvexHullShape(entityBuilder.getModel());
        final DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), position, 1)));
        final Vector3f inertia = new Vector3f();
        shape.calculateLocalInertia(1.0F, inertia);
        final RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(1.0F, motionState, shape, inertia);
        constructionInfo.restitution = 0.75F;
        final RigidBody body = new RigidBody(constructionInfo);
        dynamicsWorld.addRigidBody(body);
        return body;
    }
    
    private static Entity createNewShape(EntityBuilder entityBuilder) {
        final RigidBody body = createNewRigidBody(entityBuilder);
        final Entity entity = createBallEntity(entityBuilder, body);
        balls.put(entity, body);
        OmniKryptecEngine.instance().getCurrentScene().addGameObject(entity);
        return entity;
    }
    
    private static final float physicsSpeedStep = 0.001F;
    private static float lastTime = 0;
    
    private static void input() {
        applyForce = InputUtil.isKeyboardKeyDown(Keyboard.KEY_F);
        createNewShape = InputUtil.isKeyboardKeyDown(Keyboard.KEY_N);
        resetControlBall = InputUtil.isKeyboardKeyDown(Keyboard.KEY_R);
        if(InputUtil.isKeyboardKeyDown(Keyboard.KEY_P)) {
            float currentTime = DisplayManager.instance().getCurrentTime();
            float deltaTime = (currentTime - lastTime);
            if(deltaTime > 250) {
                physicsPause = !physicsPause;
                lastTime = currentTime;
            }
        }
        float deltaPhysicsSpeedStep = (InputUtil.isKeyboardKeyDown(Keyboard.KEY_COMMA) ? physicsSpeedStep : 0) + (InputUtil.isKeyboardKeyDown(Keyboard.KEY_PERIOD) ? -physicsSpeedStep : 0);
        physicsSpeed += deltaPhysicsSpeedStep;
        if(physicsSpeed < 0) {
            physicsSpeed = 0;
        }
        if(!(OmniKryptecEngine.instance().getCurrentScene().getCamera() instanceof FollowingCamera) && OmniKryptecEngine.instance().getDisplayManager().getSettings().getKeySettings().getKey("mouseButtonLeft").isPressed()) {
            float deltaX = InputUtil.getMouseDelta().x;
            float deltaY = InputUtil.getMouseDelta().y;
            float deltaD = InputUtil.getMouseDelta().z;
            if(InputUtil.isKeyboardKeyDown(Keyboard.KEY_LCONTROL)) {
                Camera camera = OmniKryptecEngine.getInstance().getCurrentScene().getCamera();
                InputUtil.moveXZ(camera, camera, -deltaY / 15, -deltaX / 15, deltaD);
            } else {
                OmniKryptecEngine.getInstance().getCurrentScene().getCamera().getRelativeRotation().y -= (deltaX / 5);
                OmniKryptecEngine.getInstance().getCurrentScene().getCamera().getRelativeRotation().x += (deltaY / 5);
            }
        }
    }
    
    private static Entity createBallEntity(EntityBuilder entityBuilder, RigidBody body) {
        return new Entity(entityBuilder.createTexturedModel()) {
            
            @Override
            public void doLogic() {
                final Vector3f ballPosition = body.getMotionState().getWorldTransform(new Transform()).origin;
                setRelativePos(ballPosition.x, ballPosition.y, ballPosition.z);
                final Quat4f ballOrientation = body.getOrientation(new Quat4f());
                setRotation(new org.lwjgl.util.vector.Vector3f(ballOrientation.x, ballOrientation.y, ballOrientation.z));
            }
            
            @Override
            public void delete() {
                dynamicsWorld.removeRigidBody(body);
                body.destroy();
            }
            
        };
    }
    
    private static EntityBuilder entityBuilder_brunnen;
    private static EntityBuilder entityBuilder_pine;
    
    public static void main(String[] args) {
        try {
            NativesLoader.loadNatives();
            OmniKryptecEngine.addShutdownHook(() -> NativesLoader.unloadNatives());
            Logger.enableLoggerRedirection(true);
            Logger.setDebugMode(true);
            Logger.CONSOLE.setExitWhenLastOne(true);
            Logger.showConsoleDirect();
            
            DisplayManager.createDisplay("JBullet Test", new GameSettings("JBulletTest", 1280, 720).setAnisotropicLevel(32).setMultisamples(32));

            //PostProcessing.instance().addStage(new LightRenderer());
            OmniKryptecEngine.instance().addAndSetScene("TestScene", new Scene(new Camera() {   
                @Override
                public void doLogic() {
                    InputUtil.doFirstPersonController(this, DisplayManager.instance().getSettings().getKeySettings(), 1.5F, 15.0F);
                }
                
            }.setPerspectiveProjection(75, 1000, 0.1F)));

            OmniKryptecEngine.instance().addAndSetScene("TestScene", new Scene(getCamera(1))); //TODO Set this to 0 for the firstPerson or to 1 for the thirdPerson mode
            entityBuilder_brunnen = new EntityBuilder().loadModel("/omnikryptec/test/brunnen.obj").loadTexture("/omnikryptec/test/brunnen.png");
            entityBuilder_pine = new EntityBuilder().loadModel("/omnikryptec/test/pine.obj").loadTexture("/omnikryptec/test/pine2.png");
            final Entity entity_1 = entityBuilder_brunnen.create();
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
            final Entity entity_2 = createNewShape(entityBuilder_pine);
            if(OmniKryptecEngine.instance().getCurrentScene().getCamera() instanceof FollowingCamera) {
                RigidBody lustig = createNewRigidBody(entityBuilder_brunnen);
                Entity followedEntity = new Entity(entityBuilder_brunnen.createTexturedModel()) {
                    
                    @Override
                    public void doLogic() {
                        InputUtil.doThirdPersonController(OmniKryptecEngine.instance().getCurrentScene().getCamera(), this, DisplayManager.instance().getSettings().getKeySettings(), 5.0F, 40.0F); //Standard: 1.5F, 15.0F (But too slow) //5.0F, 40.0F is better
                        lustig.setCenterOfMassTransform(new Transform(new Matrix4f(new Quat4f(getAbsoluteRotation().x, getAbsoluteRotation().y, getAbsoluteRotation().z, 1), ConverterUtil.convertVector3fFromLWJGL(getAbsolutePos()), 1.0F)));
                        lustig.setAngularVelocity(new Vector3f(0, 0, 0));
                        lustig.setLinearVelocity(new Vector3f(0, 0, 0));
                    }
                    
                    @Override
                    public void delete() {
                        dynamicsWorld.removeRigidBody(lustig);
                        lustig.destroy();
                    }
                    
                };
                
                OmniKryptecEngine.instance().getCurrentScene().addGameObject(followedEntity);
                ((FollowingCamera) OmniKryptecEngine.getInstance().getCurrentScene().getCamera()).setFollowedGameObject(followedEntity);
            }
            InputUtil.setCamera(OmniKryptecEngine.instance().getCurrentScene().getCamera());
            OmniKryptecEngine.instance().startLoop(OmniKryptecEngine.ShutdownOption.JAVA);
        } catch (Exception ex) {
            Logger.logErr("Main Error: " + ex, ex);
        }
    }
    
    private static Camera getCamera(int camera) {
        switch(camera) {
            case 0:
                return new Camera() {

                    @Override
                    public void doLogic() {
                        InputUtil.doFirstPersonController(this, DisplayManager.instance().getSettings().getKeySettings(), 1.5F, 15.0F);
                    }

                }.setPerspectiveProjection(75, 1000, 0.1F);
            case 1:
                return new FollowingCamera() {
                    
                    @Override
                    public void doLogic() {
                        move();
                    }
                    
                }.setPerspectiveProjection(75, 1000, 0.1F);
            default:
                return null;
        }
    }
    
}
