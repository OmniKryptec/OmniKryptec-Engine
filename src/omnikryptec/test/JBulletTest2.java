package omnikryptec.test;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import javax.vecmath.Vector3f;
import omnikryptec.component.PhysicsComponent;
import omnikryptec.display.DisplayManager;
import omnikryptec.entity.Camera;
import omnikryptec.entity.Entity;
import omnikryptec.entity.EntityBuilder;
import omnikryptec.event.EventSystem;
import omnikryptec.event.EventType;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene;
import omnikryptec.physics.RigidBodyBuilder;
import omnikryptec.settings.GameSettings;
import omnikryptec.util.ConverterUtil;
import omnikryptec.util.InputUtil;
import omnikryptec.util.NativesLoader;
import omnikryptec.util.PhysicsUtil;
import org.lwjgl.input.Keyboard;

/**
 *
 * @author Panzer1119
 */
public class JBulletTest2 {
    
    private static EntityBuilder entityBuilder_brunnen;
    private static EntityBuilder entityBuilder_pine;
    private static Entity entity_1;
    private static RigidBodyBuilder rigidBodyBuilder;
    
    public static final void main(String[] args) {
        try {
            NativesLoader.loadNatives();
            OmniKryptecEngine.addShutdownHook(() -> NativesLoader.unloadNatives());
            Logger.enableLoggerRedirection(true);
            Logger.setDebugMode(false);
            Logger.CONSOLE.setExitWhenLastOne(true);
            Logger.showConsoleDirect();
            
            DisplayManager.createDisplay("JBullet Test", new GameSettings("JBulletTest", 1280, 720).setAnisotropicLevel(32).setMultisamples(32));
            OmniKryptecEngine.instance().addAndSetScene("Test-Scene", new Scene(new Camera() {   
                @Override
                public void doLogic() {
                    InputUtil.doFirstPersonController(this, DisplayManager.instance().getSettings().getKeySettings(), 5.0F, 40.0F);
                }
                
            }.setPerspectiveProjection(75, 1000, 0.1F)));
            entityBuilder_brunnen = new EntityBuilder().loadModel("/omnikryptec/test/brunnen.obj").loadTexture("/omnikryptec/test/brunnen.png");
            entityBuilder_pine = new EntityBuilder().loadModel("/omnikryptec/test/pine.obj").loadTexture("/omnikryptec/test/pine2.png");
            OmniKryptecEngine.getInstance().getCurrentScene().useDefaultPhysics();
            setupStaticPlane();
            setupRigidBodyBuilder();
            entity_1 = entityBuilder_brunnen.create();
            OmniKryptecEngine.getInstance().getCurrentScene().addGameObject(entity_1);
            OmniKryptecEngine.getInstance().getCurrentScene().getCamera().getRelativePos().y += 3;
            OmniKryptecEngine.getInstance().getCurrentScene().getCamera().getRelativeRotation().x = 0;
            entity_1.addComponent(new PhysicsComponent(entity_1, rigidBodyBuilder));
            EventSystem.instance().addEventHandler(e -> input(), EventType.RENDER_EVENT);
            InputUtil.setCamera(OmniKryptecEngine.getInstance().getCurrentScene().getCamera());
            OmniKryptecEngine.getInstance().startLoop(OmniKryptecEngine.ShutdownOption.JAVA);
        } catch (Exception ex) {
            Logger.logErr("Main error: " + ex, ex);
        }
    }
    
    private static final void setupStaticPlane() {
        RigidBodyBuilder rigidBodyBuilder = new RigidBodyBuilder();
        rigidBodyBuilder.setCollisionShape(new StaticPlaneShape(new Vector3f(0, 1, 0), 0.25F/*m*/));
        rigidBodyBuilder.setDefaultMotionState(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
        rigidBodyBuilder.getRigidBodyConstructionInfo().restitution = 0.25F;
        OmniKryptecEngine.getInstance().getCurrentScene().getPhysicsWorld().getWorld().addRigidBody(rigidBodyBuilder.create());
    }
    
    private static final void setupRigidBodyBuilder() {
        final Camera camera = OmniKryptecEngine.getInstance().getCurrentScene().getCamera();
        rigidBodyBuilder = new RigidBodyBuilder(1.0F);
        rigidBodyBuilder.setCollisionShape(PhysicsUtil.createConvexHullShape(entityBuilder_brunnen.getModel()));
        rigidBodyBuilder.setDefaultMotionState(new Vector3f(camera.getAbsolutePos().x, 20.0F, camera.getAbsolutePos().z - 5), new Vector3f(0, 0, 0));
        rigidBodyBuilder.getCollisionShape().calculateLocalInertia(rigidBodyBuilder.getMass(), rigidBodyBuilder.getInertia());
        rigidBodyBuilder.getRigidBodyConstructionInfo().restitution = 0.75F;
    }
    
    private static final void input() {
        if(InputUtil.isKeyboardKeyDown(Keyboard.KEY_F)) {
            applyForce();
        }
        final float deltaX = InputUtil.getMouseDelta().x;
        final float deltaY = InputUtil.getMouseDelta().y;
        final float deltaD = InputUtil.getMouseDelta().z;
        if(OmniKryptecEngine.getInstance().getDisplayManager().getSettings().getKeySettings().getKey("mouseButtonLeft").isPressed()) {
            if(InputUtil.isKeyboardKeyDown(Keyboard.KEY_LCONTROL)) {
                Camera camera = OmniKryptecEngine.getInstance().getCurrentScene().getCamera();
                InputUtil.moveXZ(camera, camera, -deltaY / 15, -deltaX / 15, deltaD);
            } else {
                OmniKryptecEngine.getInstance().getCurrentScene().getCamera().getRelativeRotation().y -= (deltaX / 5);
                OmniKryptecEngine.getInstance().getCurrentScene().getCamera().getRelativeRotation().x += (deltaY / 5);
            }
        }
    }
    
    private static final void applyForce() {
        final Camera camera = OmniKryptecEngine.getInstance().getCurrentScene().getCamera();
        final RigidBody body = entity_1.getComponent(PhysicsComponent.class).getBody();
        final Transform bodyTransform = new Transform();
        body.getMotionState().getWorldTransform(bodyTransform);
        final Vector3f bodyLocation = bodyTransform.origin;
        final Vector3f cameraPosition = ConverterUtil.convertVector3fFromLWJGL(camera.getAbsolutePos());
        final Vector3f force = new Vector3f();
        force.sub(cameraPosition, bodyLocation);
        body.activate();
        body.applyCentralForce(force);
    }
    
}
