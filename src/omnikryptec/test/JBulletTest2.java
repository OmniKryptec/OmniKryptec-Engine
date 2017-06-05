package omnikryptec.test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.vecmath.Vector3f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

import omnikryptec.audio.AudioManager;
import omnikryptec.audio.AudioSource;
import omnikryptec.component.AudioListenerComponent;
import omnikryptec.component.AudioSourceComponent;
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
import omnikryptec.objConverter.ModelData;
import omnikryptec.physics.RigidBodyBuilder;
import omnikryptec.settings.GameSettings;
import omnikryptec.settings.Key;
import omnikryptec.settings.KeyGroup;
import omnikryptec.settings.KeySettings;
import omnikryptec.terrain.HeightsGeneratorHMap;
import omnikryptec.terrain.Terrain;
import omnikryptec.terrain.TerrainTexturePack;
import omnikryptec.texture.Texture;
import omnikryptec.util.ConverterUtil;
import omnikryptec.util.InputUtil;
import omnikryptec.util.NativesLoader;
import omnikryptec.util.PhysicsUtil;
import omnikryptec.util.RenderUtil;

/**
 *
 * @author Panzer1119
 */
public class JBulletTest2 {
    
    private static EntityBuilder entityBuilder_brunnen;
    private static EntityBuilder entityBuilder_pine;
    private static Entity entity_ball;
    private static Entity entity_attractor;
    private static RigidBodyBuilder rigidBodyBuilder_ball;
    private static RigidBodyBuilder rigidBodyBuilder_attractor;
    private static RigidBodyBuilder rigidBodyBuilder_terrain;
    private static final ArrayList<Terrain> terrains = new ArrayList<>();
    private static AudioSource bouncer;
    private static boolean isWireframe = false;
    private static BufferedImage heightMap = null;
    
    public static final void main(String[] args) {
        try {
            NativesLoader.loadNatives();
            OmniKryptecEngine.addShutdownHook(() -> NativesLoader.unloadNatives());
            Logger.enableLoggerRedirection(true);
            Logger.setDebugMode(false);
            Logger.CONSOLE.setExitWhenLastOne(true);
            Logger.showConsoleDirect();
            
            final GameSettings gameSettings = new GameSettings("JBulletTest", 1280, 720).setAnisotropicLevel(32).setMultisamples(32).setChunkSize(400, 400, 400);
            final KeySettings keySettings = gameSettings.getKeySettings();
            keySettings.setKey("pauseAudio", Keyboard.KEY_P, true);
            keySettings.setKey("toggleWireframe", Keyboard.KEY_T, true);
            keySettings.setKey(new KeyGroup("physicsPause", new Key("leftControl", Keyboard.KEY_LCONTROL, true), new Key("p", Keyboard.KEY_P, true)));
            keySettings.setKey(new KeyGroup("test_1", new Key("t_1", Keyboard.KEY_J, true)));
            keySettings.setKey(new KeyGroup("test_2", new Key("t_1", Keyboard.KEY_J, true), new Key("t_2", Keyboard.KEY_K, true)));
            keySettings.setKey("grabMouse", Keyboard.KEY_G, true);
            DisplayManager.createDisplay("JBullet Test", gameSettings);
            DisplayManager.instance().getSettings().getKeySettings().setKey("sprint", Keyboard.KEY_LCONTROL, true);
            OmniKryptecEngine.instance().addAndSetScene("Test-Scene", new Scene((Camera) new Camera() {
                
                @Override
                public void doLogic() {
                    float horizontalSpeed = 30.0F;
                    float verticalSpeed = 10.0F;
                    float turnSpeed = 40.0F;
                    if(DisplayManager.instance().getSettings().getKeySettings().getKey("sprint").isPressed()) {
                        horizontalSpeed *= 10;
                        verticalSpeed *= 10;
                    }
                    InputUtil.doThirdPersonController(this, this, DisplayManager.instance().getSettings().getKeySettings(), horizontalSpeed, verticalSpeed, turnSpeed);
                }
                
            }.setPerspectiveProjection(75, 1000, 0.1F).addComponent(new AudioListenerComponent())));
            entityBuilder_brunnen = new EntityBuilder().loadModel("/omnikryptec/test/brunnen.obj").loadTexture("/omnikryptec/test/brunnen.png");
            entityBuilder_pine = new EntityBuilder().loadModel("/omnikryptec/test/pine.obj").loadTexture("/omnikryptec/test/pine2.png");
            final Texture backgroundTexture = Texture.newTexture("/omnikryptec/terrain/grassy2.png").create();
            final Texture rTexture = Texture.newTexture("/omnikryptec/terrain/mud.png").create();
            final Texture gTexture = Texture.newTexture("/omnikryptec/terrain/grassFlowers.png").create();
            final Texture bTexture = Texture.newTexture("/omnikryptec/terrain/path.png").create();
            final Texture blendMap = Texture.newTexture("/omnikryptec/terrain/blendMap.png").create();
            try {
                heightMap = ImageIO.read(Terrain.class.getResourceAsStream("/omnikryptec/terrain/heightmap.png"));
            } catch (Exception ex) {
                Logger.logErr("Error while loading the heightmap: " + ex, ex);
                heightMap = null;
            }
            AudioManager.loadSound("bounce", "/omnikryptec/audio/bounce.wav");
            OmniKryptecEngine.getInstance().getCurrentScene().useDefaultPhysics();
            setupStaticPlane();
            setupRigidBodyBuilder();
            entity_ball = entityBuilder_brunnen.create();
            entity_attractor = entityBuilder_pine.create();
            final TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
            setupTerrains(texturePack, blendMap, 4);
            addTerrains();
            OmniKryptecEngine.getInstance().getCurrentScene().addGameObject(entity_ball);
            OmniKryptecEngine.getInstance().getCurrentScene().addGameObject(entity_attractor);
            OmniKryptecEngine.getInstance().getCurrentScene().getCamera().getRelativePos().y += 3;
            OmniKryptecEngine.getInstance().getCurrentScene().getCamera().getRelativeRotation().y = 90;
            manageTerrains();
            entity_ball.addComponent(new PhysicsComponent(entity_ball, rigidBodyBuilder_ball));
            bouncer = new AudioSource().setLooping(true);
            bouncer.setRollOffFactor(0.5F);
            bouncer.play("bounce");
            entity_ball.addComponent(new AudioSourceComponent(bouncer));
            entity_attractor.addComponent(new PhysicsComponent(entity_attractor, rigidBodyBuilder_attractor));
            EventSystem.instance().addEventHandler(e -> {input(); logic();}, EventType.RENDER_EVENT);
            InputUtil.setCamera(OmniKryptecEngine.getInstance().getCurrentScene().getCamera());
            InputUtil.setLongButtonPressEnabled(true);
            OmniKryptecEngine.getInstance().startLoop(OmniKryptecEngine.ShutdownOption.JAVA);
        } catch (Exception ex) {
            Logger.logErr("Main error: " + ex, ex);
        }
    }
    
    private static final void setupTerrains(TerrainTexturePack texturePack, Texture blendMap, int count) {
        for(int i = 0; i < count; i++) {
            final ModelData modelData = Terrain.generateTerrain(i*heightMap.getWidth(), -i*heightMap.getWidth(), new HeightsGeneratorHMap(heightMap, 40.0F, true), heightMap.getWidth(), heightMap.getWidth());
            final Terrain terrain = new Terrain(i*heightMap.getWidth(), -i*heightMap.getWidth(), modelData, texturePack, blendMap);
            //final Terrain terrain = new Terrain(i, -i, texturePack, blendMap);
            terrains.add(terrain);
        }
    }
    
    private static final void addTerrains() {
        for(Terrain terrain : terrains) {
            OmniKryptecEngine.getInstance().getCurrentScene().addGameObject(terrain);
        }
    }
    
    private static final void manageTerrains() {
        for(Terrain terrain : terrains) {
            rigidBodyBuilder_terrain.setCollisionShape(PhysicsUtil.createConvexHullShape(terrain.getTexturedModel().getModel()));
            rigidBodyBuilder_terrain.setDefaultMotionState(new Vector3f(terrain.getAbsolutePos().x / 2, 0, terrain.getAbsolutePos().z / 2), new Vector3f(0, 0, 0));
            terrain.addComponent(new PhysicsComponent(terrain, rigidBodyBuilder_terrain).setPause(true));
        }
    }
    
    private static final void setupStaticPlane() {
        if(true) {
            return;
        }
        RigidBodyBuilder rigidBodyBuilder = new RigidBodyBuilder();
        rigidBodyBuilder.setCollisionShape(new StaticPlaneShape(new Vector3f(0, 1, 0), 0.25F/*m*/));
        rigidBodyBuilder.setDefaultMotionState(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
        rigidBodyBuilder.getRigidBodyConstructionInfo().restitution = 0.25F;
        OmniKryptecEngine.getInstance().getCurrentScene().getPhysicsWorld().getWorld().addRigidBody(rigidBodyBuilder.create());
    }
    
    private static final void setupRigidBodyBuilder() {
        final Camera camera = OmniKryptecEngine.getInstance().getCurrentScene().getCamera();
        rigidBodyBuilder_ball = new RigidBodyBuilder(1.0F);
        rigidBodyBuilder_ball.setCollisionShape(PhysicsUtil.createConvexHullShape(entityBuilder_brunnen.getModel()));
        rigidBodyBuilder_ball.setDefaultMotionState(new Vector3f(camera.getAbsolutePos().x, 20.0F, camera.getAbsolutePos().z - 5), new Vector3f(0, 0, 0));
        rigidBodyBuilder_ball.getCollisionShape().calculateLocalInertia(rigidBodyBuilder_ball.getMass(), rigidBodyBuilder_ball.getInertia());
        rigidBodyBuilder_ball.getRigidBodyConstructionInfo().restitution = 0.75F;
        rigidBodyBuilder_attractor = new RigidBodyBuilder(1000.0F);
        rigidBodyBuilder_attractor.setCollisionShape(PhysicsUtil.createConvexHullShape(entityBuilder_pine.getModel()));
        rigidBodyBuilder_attractor.setDefaultMotionState(new Vector3f(camera.getAbsolutePos().x, 10.0F, camera.getAbsolutePos().z - 30), new Vector3f(0, 0, 0));
        rigidBodyBuilder_attractor.getCollisionShape().calculateLocalInertia(rigidBodyBuilder_attractor.getMass(), rigidBodyBuilder_attractor.getInertia());
        rigidBodyBuilder_attractor.getRigidBodyConstructionInfo().restitution = 0.75F;
        rigidBodyBuilder_terrain = new RigidBodyBuilder(0.0F);
        rigidBodyBuilder_terrain.setCollisionShape(new StaticPlaneShape(new Vector3f(0, 1, 0), 0.25F));
        rigidBodyBuilder_terrain.setDefaultMotionState(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
        rigidBodyBuilder_terrain.getRigidBodyConstructionInfo().restitution = 0.25F;
    }
    
    private static final void logic() {
        //applyCircularForce();
        if(true) {
            return;
        }
        final RigidBody body = entity_ball.getComponent(PhysicsComponent.class).getBody();
        final Transform bodyTransform = new Transform();
        body.getMotionState().getWorldTransform(bodyTransform);
        final Vector3f bodyLocation = bodyTransform.origin;
        final Vector3f attractorPosition = ConverterUtil.convertVector3fFromLWJGL(entity_attractor.getAbsolutePos());
        final Vector3f force = new Vector3f();
        force.sub(attractorPosition, bodyLocation);
        body.activate();
        final float attractorFactor = 0.5F;
        body.applyCentralForce(new Vector3f(force.x * attractorFactor, force.y * attractorFactor, force.z * attractorFactor));
    }
    
    private static final void input() {
        final GameSettings gameSettings = DisplayManager.instance().getSettings();
        final KeySettings keySettings = gameSettings.getKeySettings();
        if(keySettings.isPressed("test_1")) {
            Logger.log(keySettings.getKeyGroup("test_1"));
        }
        if(keySettings.isPressed("test_2")) {
            Logger.log(keySettings.getKeyGroup("test_2"));
        }
        Camera camera = OmniKryptecEngine.getInstance().getCurrentScene().getCamera();
        if(InputUtil.isKeyboardKeyDown(Keyboard.KEY_F)) {
            applyForce();
        }
        if(InputUtil.isKeyboardKeyDown(Keyboard.KEY_C)) {
            final RigidBody body = entity_ball.getComponent(PhysicsComponent.class).getBody();
            body.setCenterOfMassTransform(PhysicsUtil.createTransform(ConverterUtil.convertVector3fFromLWJGL(camera.getAbsolutePos()), new Vector3f(0, 0, 0)));
            body.setAngularVelocity(new Vector3f(0, 0, 0));
            body.setLinearVelocity(new Vector3f(0, 0, 0));
        }
        if(keySettings.getKey("pauseAudio").isLongPressed(100, 200)) {
            if(bouncer.isPlaying()) {
                bouncer.pause();
            } else {
                bouncer.continuePlaying();
            }
        }
        final Scene scene = OmniKryptecEngine.getInstance().getCurrentScene();
        if(scene != null && scene.isUsingPhysics() && keySettings.getKeyGroup("physicsPause").isLongPressed(100, 500)) {
            scene.getPhysicsWorld().setSimulationPaused(!scene.getPhysicsWorld().isSimulationPaused());
        }
        if(keySettings.getKey("toggleWireframe").isLongPressed(100, 200)) {
            isWireframe = !isWireframe;
            RenderUtil.goWireframe(isWireframe);
        }
        if(keySettings.getKey("grabMouse").isLongPressed(100, 200)) {
            Mouse.setGrabbed(!Mouse.isGrabbed());
        }
        float deltaX = InputUtil.getMouseDelta().x;
        float deltaY = InputUtil.getMouseDelta().y;
        float deltaD = InputUtil.getMouseDelta().z;
        if(Mouse.isGrabbed()) {
            deltaX *= -1;
            deltaY *= -1;
            deltaD *= 1;
        }
        if(keySettings.getKey("mouseButtonLeft").isPressed() || Mouse.isGrabbed()) {
            if(InputUtil.isKeyboardKeyDown(Keyboard.KEY_L)) {
                InputUtil.moveXZ(camera, camera, -deltaY / 15, -deltaX / 15, deltaD);
            } else {
                OmniKryptecEngine.getInstance().getCurrentScene().getCamera().getRelativeRotation().y -= (deltaX / 5);
                OmniKryptecEngine.getInstance().getCurrentScene().getCamera().getRelativeRotation().x += (deltaY / 5);
            }
        }
    }
    
    private static final void applyForce() {
        final Camera camera = OmniKryptecEngine.getInstance().getCurrentScene().getCamera();
        final RigidBody body = entity_ball.getComponent(PhysicsComponent.class).getBody();
        final Transform bodyTransform = new Transform();
        body.getMotionState().getWorldTransform(bodyTransform);
        final Vector3f bodyLocation = bodyTransform.origin;
        final Vector3f cameraPosition = ConverterUtil.convertVector3fFromLWJGL(camera.getAbsolutePos());
        final Vector3f force = new Vector3f();
        force.sub(cameraPosition, bodyLocation);
        body.activate();
        body.applyCentralForce(force);
    }
    
    private static final void applyCircularForce() {
        final Camera camera = OmniKryptecEngine.getInstance().getCurrentScene().getCamera();
        final RigidBody body = entity_ball.getComponent(PhysicsComponent.class).getBody();
        final Transform bodyTransform = new Transform();
        body.getMotionState().getWorldTransform(bodyTransform);
        final Vector3f bodyLocation = bodyTransform.origin;
        final Vector3f cameraPosition = ConverterUtil.convertVector3fFromLWJGL(camera.getAbsolutePos());
        final Vector3f force = new Vector3f();
        force.sub(cameraPosition, bodyLocation);
        Vector3f temp = new Vector3f();
        temp.cross(bodyLocation, ConverterUtil.convertVector3fFromLWJGL(entity_ball.getAbsoluteRotation()));
        temp.dot(force);
        Logger.log(temp);
        body.activate();
        //body.applyCentralForce(temp);
    }
    
}
