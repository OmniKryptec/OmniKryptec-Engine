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
import omnikryptec.camera.MatrixMath;
import omnikryptec.display.DisplayManager;
import omnikryptec.display.GameSettings;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene;
import omnikryptec.objConverter.ObjLoader;
import omnikryptec.storing.Entity;
import omnikryptec.storing.Model;
import omnikryptec.storing.TexturedModel;
import omnikryptec.texture.Texture;
import omnikryptec.util.InputUtil;
import omnikryptec.util.NativesLoader;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.glu.Sphere;

/**
 *
 * @author Panzer1119
 */
public class JBulletTest {
    
    private static final Transform DEFAULT_BALL_TRANSFORM = new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, 35, 0), 1.0F));
    
    private static DynamicsWorld dynamicsWorld;
    private static final HashMap<Entity, RigidBody> balls = new HashMap<>();
    private static RigidBody controlBall;
    private static boolean applyForce = false;
    private static boolean createNewShape = false;
    private static boolean resetControlBall = false;
    private static Sphere sphere = new Sphere();
    
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
            balls.put(createBallEntity(controlBall), controlBall);
            dynamicsWorld.addRigidBody(controlBall);
            
        } catch (Exception ex) {
            Logger.logErr("Error while setting up physics: " + ex, ex);
        }
    }
    
    private static void render() {
        Logger.log("Rendering balls");
        /*
        for(RigidBody body : balls.values()) {
            GL11.glPushMatrix();
            final Vector3f ballPosition = body.getWorldTransform(new Transform()).origin;
            GL11.glTranslatef(ballPosition.x, ballPosition.y, ballPosition.z);
            sphere.setDrawStyle(GLU.GLU_SILHOUETTE);
            if(body.equals(controlBall)) {
                GL11.glColor4f(0, 1, 0, 1);
            }
            sphere.draw(3.0F, 30, 30); //m
            GL11.glPopMatrix();
        }
        */
        /*//GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(0.6F, 0.6F, 0.6F, 1);
        GL11.glVertex3f(-50, 0, -50);
        GL11.glColor4f(0.85F, 0.85F, 0.85F, 1);
        GL11.glVertex3f(-50, 0, 50);
        GL11.glColor4f(0.85F, 0.75F, 0.75F, 1);
        GL11.glVertex3f(50, 0, 50);
        GL11.glColor4f(0.5F, 0.5F, 0.5F, 1);
        GL11.glVertex3f(50, 0, -50);
        //GL11.glEnd();*/
    }
    
    private static Entity createBallEntity(RigidBody body) {
        return new Entity(brunnen_tm) {
            
            @Override
            public void doLogic() {
                final Vector3f ballPosition = body.getWorldTransform(new Transform()).origin;
                setRelativePos(ballPosition.x, ballPosition.y, ballPosition.z);
                final Quat4f ballOrientation = body.getOrientation(new Quat4f());
                setRotation(new org.lwjgl.util.vector.Vector3f(ballOrientation.x, ballOrientation.y, ballOrientation.z));
            }
            
        };
    }
    
    private static Model brunnen_model;
    private static Texture brunnen_texture;
    private static TexturedModel brunnen_tm;
    
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
                    doCameraLogic(this);
                }
                
            }));
            brunnen_model = new Model(ObjLoader.loadNMOBJ(JBulletTest.class.getResourceAsStream("/omnikryptec/test/brunnen.obj")));
            brunnen_texture = Texture.newTexture(JBulletTest.class.getResourceAsStream("/omnikryptec/test/brunnen.png")).create();
            brunnen_tm = new TexturedModel(brunnen_model, brunnen_texture);
            final Entity entity_1 = new Entity(brunnen_tm);
            OmniKryptecEngine.instance().getCurrentScene().addGameObject(entity_1);
            OmniKryptecEngine.instance().getCurrentScene().getCamera().getRelativePos().y += 3;
            OmniKryptecEngine.instance().getCurrentScene().getCamera().getRelativeRotation().x = 40;
            entity_1.setRelativePos(0, 0, -5);
            //EventSystem.instance().addEventHandler(e -> render(), EventType.RENDER_EVENT);
            /*OmniKryptecEngine.instance().getCurrentScene().addGameObject(new Entity(brunnen_tm) {
                
                @Override
                public void doLogic() {
                    render();
                }
                
            });*/
            setUpPhysics();
            for(Entity e : balls.keySet()) {
                OmniKryptecEngine.instance().getCurrentScene().addGameObject(e);
            }
            OmniKryptecEngine.instance().startLoop(OmniKryptecEngine.ShutdownOption.JAVA);
        } catch (Exception ex) {
            Logger.logErr("Main Error: " + ex, ex);
        }
    }
    
    private static void doCameraLogic(Camera camera) {
        final float deltaPos = (0.4F * DisplayManager.instance().getDeltaTime());
        final float deltaRot = (10.0F * DisplayManager.instance().getDeltaTime());
        if(InputUtil.isKeyDown(Keyboard.KEY_W)) {
            camera.setRelativePos(camera.getRelativePos().x,            camera.getRelativePos().y, (float) ((camera.getRelativePos().z - deltaPos) * Math.sin(Math.toRadians(camera.getRelativeRotation().y))));
        }
        if(InputUtil.isKeyDown(Keyboard.KEY_S)) {
            camera.setRelativePos(camera.getRelativePos().x,            camera.getRelativePos().y,              camera.getRelativePos().z + deltaPos);
        }
        if(InputUtil.isKeyDown(Keyboard.KEY_A)) {
            camera.setRelativePos(camera.getRelativePos().x - deltaPos, camera.getRelativePos().y,              camera.getRelativePos().z);
        }
        if(InputUtil.isKeyDown(Keyboard.KEY_D)) {
            camera.setRelativePos(camera.getRelativePos().x + deltaPos, camera.getRelativePos().y,              camera.getRelativePos().z);
        }
        if(InputUtil.isKeyDown(Keyboard.KEY_LSHIFT)) {
            camera.setRelativePos(camera.getRelativePos().x,            camera.getRelativePos().y - deltaPos,   camera.getRelativePos().z);
        }
        if(InputUtil.isKeyDown(Keyboard.KEY_SPACE)) {
            camera.setRelativePos(camera.getRelativePos().x,            camera.getRelativePos().y + deltaPos,   camera.getRelativePos().z);
        }
        if(InputUtil.isKeyDown(Keyboard.KEY_LEFT)) {
            camera.getRelativeRotation().z += deltaRot;
        }
        if(InputUtil.isKeyDown(Keyboard.KEY_RIGHT)) {
            camera.getRelativeRotation().z -= deltaRot;
        }
        if(InputUtil.isKeyDown(Keyboard.KEY_UP)) {
            camera.getRelativeRotation().x -= deltaRot;
        }
        if(InputUtil.isKeyDown(Keyboard.KEY_DOWN)) {
            camera.getRelativeRotation().x += deltaRot;
        }
        Logger.CONSOLE.setTitle(camera.toString());
    }
    
}
