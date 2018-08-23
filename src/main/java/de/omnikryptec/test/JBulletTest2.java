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

package de.omnikryptec.test;

import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import de.omnikryptec.audio.AudioEffectState;
import de.omnikryptec.audio.AudioManager;
import de.omnikryptec.audio.AudioSource;
import de.omnikryptec.audio.StreamedSound;
import de.omnikryptec.display.DisplayManager;
import de.omnikryptec.display.GLFWInfo;
import de.omnikryptec.event.input.CursorType;
import de.omnikryptec.event.input.InputManager;
import de.omnikryptec.gameobject.Camera;
import de.omnikryptec.gameobject.Entity;
import de.omnikryptec.gameobject.EntityBuilder;
import de.omnikryptec.gameobject.GameObject3D;
import de.omnikryptec.gameobject.component.AudioListenerComponent3D;
import de.omnikryptec.gameobject.component.AudioSourceComponent3D;
import de.omnikryptec.gameobject.component.PhysicsComponent3D;
import de.omnikryptec.gameobject.terrain.Terrain;
import de.omnikryptec.gameobject.terrain.TerrainGenerator;
import de.omnikryptec.gameobject.terrain.TerrainTexturePack;
import de.omnikryptec.graphics.GraphicsUtil;
import de.omnikryptec.main.AbstractScene3D;
import de.omnikryptec.main.OmniKryptecEngine;
import de.omnikryptec.main.Scene3D;
import de.omnikryptec.physics.JBulletPhysicsWorld;
import de.omnikryptec.physics.RigidBodyBuilder;
import de.omnikryptec.resource.model.Material;
import de.omnikryptec.resource.objConverter.ModelData;
import de.omnikryptec.resource.texture.SimpleTexture;
import de.omnikryptec.settings.GameSettings;
import de.omnikryptec.settings.Key;
import de.omnikryptec.settings.KeyGroup;
import de.omnikryptec.settings.KeySettings;
import de.omnikryptec.util.ConverterUtil;
import de.omnikryptec.util.NativesLoader;
import de.omnikryptec.util.PhysicsUtil;
import de.omnikryptec.util.logger.Logger;
import de.pcfreak9000.noise.components.NoiseWrapper;
import de.pcfreak9000.noise.noises.OpenSimplexNoise;
import org.lwjgl.glfw.GLFW;

import javax.imageio.ImageIO;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

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
    private static final Random random = new Random();

    public static final void main(String[] args) {
        try {
            NativesLoader.loadNatives();
            OmniKryptecEngine.addShutdownHook(() -> NativesLoader.unloadNatives());
            Logger.enableLoggerRedirection(true);
            Logger.setDebugMode(false);
            Logger.CONSOLE.setExitWhenLastOne(true);
            Logger.showConsoleDirect();

            final GameSettings gameSettings = new GameSettings().setAnisotropicLevel(32).setMultisamples(32).setChunkSize(400, 400, 400);
            final KeySettings keySettings = gameSettings.getKeySettings();
            keySettings.setKey("pauseAudio", GLFW.GLFW_KEY_P, true);
            keySettings.setKey("toggleWireframe", GLFW.GLFW_KEY_T, true);
            keySettings.setKey(new KeyGroup("physicsPause", new Key("leftControl", GLFW.GLFW_KEY_LEFT_CONTROL, true),
                    new Key("p", GLFW.GLFW_KEY_P, true)));
            keySettings.setKey(new KeyGroup("test_1", new Key("t_1", GLFW.GLFW_KEY_J, true)));
            keySettings.setKey(
                    new KeyGroup("test_2", new Key("t_1", GLFW.GLFW_KEY_J, true), new Key("t_2", GLFW.GLFW_KEY_K, true)));
            final KeyGroup grabMouse = new KeyGroup("grabMouse", new Key("grabMouse1", GLFW.GLFW_KEY_G, true),
                    new Key("grabMouse2", GLFW.GLFW_KEY_Y, true)).setAllKeysNeedToBeActivated(false);
            keySettings.setKey(grabMouse);
            keySettings.setKey("physicsFaster", GLFW.GLFW_KEY_PERIOD, true);
            keySettings.setKey("physicsSlower", GLFW.GLFW_KEY_COMMA, true);
            DisplayManager.createDisplay("JBullet Test2", gameSettings, new GLFWInfo(4, 3, true, false, 1280, 720));
            OmniKryptecEngine.instance().getSettings().getKeySettings().setKey("sprint", GLFW.GLFW_KEY_LEFT_CONTROL, true);
            OmniKryptecEngine.instance().addAndSetScene(new Scene3D("Test-Scene", (Camera) new Camera() {

                @Override
                public void update() {
                    float horizontalSpeed = 30.0F;
                    float verticalSpeed = 10.0F;
                    float turnSpeed = 40.0F;
                    if (OmniKryptecEngine.instance().getSettings().getKeySettings().getKey("sprint").isPressed()) {
                        horizontalSpeed *= 10;
                        verticalSpeed *= 10;
                    }
                    InputManager.doThirdPersonController(this, this,
                            OmniKryptecEngine.instance().getSettings().getKeySettings(), horizontalSpeed, verticalSpeed,
                            turnSpeed);
                }

            }.setPerspectiveProjection(75, 0.1F, 1000).addComponent(new AudioListenerComponent3D())));
            entityBuilder_brunnen = new EntityBuilder().loadModel("/de/omnikryptec/test/brunnen.obj")
                    .loadTexture("/de/omnikryptec/test/brunnen.png");
            entityBuilder_pine = new EntityBuilder().loadModel("/de/omnikryptec/test/pine.obj")
                    .loadTexture("/de/omnikryptec/test/pine2.png");
            final SimpleTexture backgroundTexture = SimpleTexture.newTextureb("/omnikryptec/gameobject/terrain/grassy2.png")
                    .create();
            final SimpleTexture rTexture = SimpleTexture.newTextureb("/omnikryptec/gameobject/terrain/mud.png").create();
            final SimpleTexture gTexture = SimpleTexture.newTextureb("/omnikryptec/gameobject/terrain/grassFlowers.png").create();
            final SimpleTexture bTexture = SimpleTexture.newTextureb("/omnikryptec/gameobject/terrain/path.png").create();
            final SimpleTexture blendMap = SimpleTexture.newTextureb("/omnikryptec/gameobject/terrain/blendMap.png").create();
            try {
                heightMap = ImageIO.read(Terrain.class.getResourceAsStream("/omnikryptec/gameobject/terrain/heightmap.png"));
            } catch (Exception ex) {
                Logger.logErr("Error while loading the heightmap: " + ex, ex);
                heightMap = null;
            }
            AudioManager.loadSound("bounce", "/omnikryptec/audio/bounce.wav");
            OmniKryptecEngine.instance().getCurrent3DScene().useDefaultPhysics();
            setupStaticPlane();
            setupRigidBodyBuilder();
            entity_ball = entityBuilder_brunnen.create();
            entity_ball.getAdvancedModel().getMaterial().setTexture(Material.DIFFUSE, entityBuilder_brunnen.getTexture());
            entity_attractor = entityBuilder_pine.create();
            final TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture,
                    bTexture);
            setupTerrains(texturePack, blendMap, 4);
            addTerrains();
            OmniKryptecEngine.instance().getCurrent3DScene().addGameObject(entity_ball);
            OmniKryptecEngine.instance().getCurrent3DScene().addGameObject(entity_attractor);
            OmniKryptecEngine.instance().getCurrent3DScene().getCamera().getTransform().increasePosition(0, 3, 0);
            OmniKryptecEngine.instance().getCurrent3DScene().getCamera().getTransform().increaseRotation(0, 90, 0);
            final AudioSource source = new AudioSource();
            final StreamedSound streamedSound = StreamedSound.ofInputStream("Tobu_-_Infectious_[NCS_Release]", source,
                    JBulletTest2.class.getResourceAsStream("/omnikryptec/audio/Tobu_-_Infectious_[NCS_Release].wav"));
            source.setAffectedByPhysics(true);
            source.play(streamedSound);
            source.setFadeTimeComplete(10000);
            source.setEffectState(AudioEffectState.FADE_OUT);
            OmniKryptecEngine.instance().getCurrent3DScene()
                    .addGameObject((GameObject3D) new GameObject3D().addComponent(new AudioSourceComponent3D(source)));
            manageTerrains();
            // entity_ball.addComponent(new PhysicsComponent(entity_ball, rigidBodyBuilder_ball));
            bouncer = new AudioSource().setLooping(true);
            bouncer.setRollOffFactor(0.5F);
            // bouncer.play("bounce");
            entity_ball.addComponent(new AudioSourceComponent3D(bouncer));
            entity_attractor.addComponent(new PhysicsComponent3D(entity_attractor, rigidBodyBuilder_attractor));
            /* //FIXME Fix the EventSystem
            OmniKryptecEngine.instance().getEventsystem().addEventHandler((e) -> {
                input();
                logic();
            }, EventType.RENDER_FRAME_EVENT);
            */
            InputManager.setCamera(OmniKryptecEngine.instance().getCurrent3DScene().getCamera());
            InputManager.setLongButtonPressEnabled(true);
            OmniKryptecEngine.instance().startLoop();
        } catch (Exception ex) {
            Logger.logErr("Main error: " + ex, ex);
        }
    }

    private static final void setupTerrains(TerrainTexturePack texturePack, SimpleTexture blendMap, int count) {
        final TerrainGenerator tgn = new TerrainGenerator(
                new NoiseWrapper(new OpenSimplexNoise()).setXScale(0.1).setYScale(0.1)).setAmplitude(40);
        // final TerrainGenerator tgn = new TerrainGeneratorHeightMap(heightMap,
        // true).setAmplitude(40);
        for (int x = 0; x < count; x++) {
            for (int y = 0; y < count; y++) {
                final ModelData modelData = Terrain.generateTerrain(x * heightMap.getWidth(), y * heightMap.getWidth(),
                        tgn, heightMap.getWidth(), heightMap.getWidth());
                final Terrain terrain = new Terrain(heightMap.getWidth(), x * heightMap.getWidth(), y * heightMap.getWidth(), "terrain", modelData, texturePack, blendMap);
                // final Terrain terrain = new Terrain(i, -i, texturePack,
                // blendMap);
                terrains.add(terrain);
            }
        }
    }

    private static final void addTerrains() {
        for (Terrain terrain : terrains) {
            OmniKryptecEngine.instance().getCurrent3DScene().addGameObject(terrain);
        }
    }

    private static final void manageTerrains() {
        for (Terrain terrain : terrains) {
            rigidBodyBuilder_terrain.setCollisionShape(PhysicsUtil.createConvexHullShape(terrain.getAdvancedModel().getModel()));
            rigidBodyBuilder_terrain.setDefaultMotionState(new Vector3f(terrain.getTransform().getPosition().x / 2, 0, terrain.getTransform().getPosition().z / 2), new Quat4f(0, 0, 0, 0));
            terrain.addComponent(new PhysicsComponent3D(terrain, rigidBodyBuilder_terrain).setPause(true));
        }
    }

    private static final void setupStaticPlane() {
        if (true) {
            return;
        }
        RigidBodyBuilder rigidBodyBuilder = new RigidBodyBuilder();
        rigidBodyBuilder.setCollisionShape(new StaticPlaneShape(new Vector3f(0, 1, 0), 0.25F/* m */));
        rigidBodyBuilder.setDefaultMotionState(new Vector3f(0, 0, 0), new Quat4f(0, 0, 0, 0));
        rigidBodyBuilder.getRigidBodyConstructionInfo().restitution = 0.25F;
        ((JBulletPhysicsWorld) OmniKryptecEngine.instance().getCurrent3DScene().getPhysicsWorld()).getWorld().addRigidBody(rigidBodyBuilder.create());
    }

    private static final void setupRigidBodyBuilder() {
        final Camera camera = OmniKryptecEngine.instance().getCurrent3DScene().getCamera();
        rigidBodyBuilder_ball = new RigidBodyBuilder(1.0F);
        rigidBodyBuilder_ball.setCollisionShape(PhysicsUtil.createConvexHullShape(entityBuilder_brunnen.getModel()));
        rigidBodyBuilder_ball.setDefaultMotionState(new Vector3f(camera.getTransform().getPosition().x, 20.0F, camera.getTransform().getPosition().z - 5), new Quat4f(0, 0, 0, 0));
        rigidBodyBuilder_ball.getCollisionShape().calculateLocalInertia(rigidBodyBuilder_ball.getMass(), rigidBodyBuilder_ball.getInertia());
        rigidBodyBuilder_ball.getRigidBodyConstructionInfo().restitution = 0.75F;
        rigidBodyBuilder_attractor = new RigidBodyBuilder(1000.0F);
        rigidBodyBuilder_attractor.setCollisionShape(PhysicsUtil.createConvexHullShape(entityBuilder_pine.getModel()));
        rigidBodyBuilder_attractor.setDefaultMotionState(new Vector3f(camera.getTransform().getPosition().x, 10.0F, camera.getTransform().getPosition().z - 30), new Quat4f(0, 0, 0, 0));
        rigidBodyBuilder_attractor.getCollisionShape().calculateLocalInertia(rigidBodyBuilder_attractor.getMass(), rigidBodyBuilder_attractor.getInertia());
        rigidBodyBuilder_attractor.getRigidBodyConstructionInfo().restitution = 0.75F;
        rigidBodyBuilder_terrain = new RigidBodyBuilder(0.0F);
        rigidBodyBuilder_terrain.setCollisionShape(new StaticPlaneShape(new Vector3f(0, 1, 0), 0.25F));
        rigidBodyBuilder_terrain.setDefaultMotionState(new Vector3f(0, 0, 0), new Quat4f(0, 0, 0, 0));
        rigidBodyBuilder_terrain.getRigidBodyConstructionInfo().restitution = 0.25F;
    }

    private static final void logic() {
        // applyCircularForce();
        if (true) {
            return;
        }
        final RigidBody body = entity_ball.getComponent(PhysicsComponent3D.class).getBody();
        final Transform bodyTransform = new Transform();
        body.getMotionState().getWorldTransform(bodyTransform);
        final Vector3f bodyLocation = bodyTransform.origin;
        final Vector3f attractorPosition = ConverterUtil.convertVector3fFromLWJGL(entity_attractor.getTransform().getPosition(true));
        final Vector3f force = new Vector3f();
        force.sub(attractorPosition, bodyLocation);
        body.activate();
        final float attractorFactor = 0.5F;
        body.applyCentralForce(
                new Vector3f(force.x * attractorFactor, force.y * attractorFactor, force.z * attractorFactor));
    }

    private static final void input() {
        final GameSettings gameSettings = OmniKryptecEngine.instance().getSettings();
        final KeySettings keySettings = gameSettings.getKeySettings();
        if (keySettings.isPressed("test_1")) {
            Logger.log(keySettings.getKeyGroup("test_1"));
        }
        if (keySettings.isPressed("test_2")) {
            Logger.log(keySettings.getKeyGroup("test_2"));
        }
        Camera camera = OmniKryptecEngine.instance().getCurrent3DScene().getCamera();
        if (InputManager.isKeyboardKeyPressed(GLFW.GLFW_KEY_F)) {
            applyForce();
        }
        if (InputManager.isKeyboardKeyPressed(GLFW.GLFW_KEY_C)) {
            final RigidBody body = entity_ball.getComponent(PhysicsComponent3D.class).getBody();
            body.setCenterOfMassTransform(PhysicsUtil.createTransform(ConverterUtil.convertVector3fFromLWJGL(camera.getTransform().getPosition(true)), new Quat4f(0, 0, 0, 0)));
            body.setAngularVelocity(new Vector3f(0, 0, 0));
            body.setLinearVelocity(new Vector3f(0, 0, 0));
        }
        if (keySettings.getKey("pauseAudio").isLongPressed(100, 200)) {
            if (bouncer.isPlaying()) {
                bouncer.pause();
            } else {
                bouncer.continuePlaying();
            }
        }
        final AbstractScene3D scene = OmniKryptecEngine.instance().getCurrent3DScene();
        float deltaPhysicsSpeed = 0;
        if (keySettings.isPressed("physicsFaster")) {
            deltaPhysicsSpeed += 0.005;
        }
        if (keySettings.isPressed("physicsSlower")) {
            deltaPhysicsSpeed -= 0.005;
        }
        scene.getPhysicsWorld().setSimulationSpeed(scene.getPhysicsWorld().getSimulationSpeed() + deltaPhysicsSpeed);
        if (scene != null && scene.isUsingPhysics()
                && keySettings.getKeyGroup("physicsPause").isLongPressed(100, 400)) {
            scene.getPhysicsWorld().setSimulationPaused(!scene.getPhysicsWorld().isSimulationPaused());
        }
        if (keySettings.getKey("toggleWireframe").isLongPressed(100, 400)) {
            isWireframe = !isWireframe;
            GraphicsUtil.goWireframe(isWireframe);
        }
        if (keySettings.isLongPressed("grabMouse", 100, 400)) {
            InputManager.setCursorType(((InputManager.getCursorType() == CursorType.DISABLED) ? CursorType.NORMAL : CursorType.DISABLED));
        }
        float deltaX = InputManager.getMouseDelta().x;
        float deltaY = InputManager.getMouseDelta().y;
        float deltaD = InputManager.getMouseDelta().z;
        if (InputManager.getCursorType() == CursorType.DISABLED) {
            deltaX *= -1;
            deltaY *= 1;
            deltaD *= 1;
        }
        if (keySettings.getKey("mouseButtonLeft").isPressed() || (InputManager.getCursorType() == CursorType.DISABLED)) {
            if (InputManager.isKeyboardKeyPressed(GLFW.GLFW_KEY_L)) {
                InputManager.moveXZ(camera, camera, -deltaY / 15, -deltaX / 15, deltaD);
            } else {
                OmniKryptecEngine.instance().getCurrent3DScene().getCamera().getTransform().increaseRotation((deltaY / 5), -(deltaX / 5), 0);
            }
        }
    }

    private static final void applyForce() {
        final Camera camera = OmniKryptecEngine.instance().getCurrent3DScene().getCamera();
        final RigidBody body = entity_ball.getComponent(PhysicsComponent3D.class).getBody();
        final Transform bodyTransform = new Transform();
        body.getMotionState().getWorldTransform(bodyTransform);
        final Vector3f bodyLocation = bodyTransform.origin;
        final Vector3f cameraPosition = ConverterUtil.convertVector3fFromLWJGL(camera.getTransform().getPosition(true));
        final Vector3f force = new Vector3f();
        force.sub(cameraPosition, bodyLocation);
        body.activate();
        body.applyCentralForce(force);
    }

    private static final void applyCircularForce() {
        final Camera camera = OmniKryptecEngine.instance().getCurrent3DScene().getCamera();
        final RigidBody body = entity_ball.getComponent(PhysicsComponent3D.class).getBody();
        final Transform bodyTransform = new Transform();
        body.getMotionState().getWorldTransform(bodyTransform);
        final Vector3f bodyLocation = bodyTransform.origin;
        final Vector3f cameraPosition = ConverterUtil.convertVector3fFromLWJGL(camera.getTransform().getPosition(true));
        final Vector3f force = new Vector3f();
        force.sub(cameraPosition, bodyLocation);
        Vector3f temp = new Vector3f();
        temp.cross(bodyLocation, ConverterUtil.convertVector3fFromLWJGL(entity_ball.getTransform().getEulerAngelsXYZ()));
        temp.dot(force);
        Logger.log(temp);
        body.activate();
        // body.applyCentralForce(temp);
    }

}
