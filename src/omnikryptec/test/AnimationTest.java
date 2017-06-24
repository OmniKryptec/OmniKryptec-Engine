package omnikryptec.test;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import omnikryptec.animation.AnimatedModel;
import omnikryptec.animation.Animation;
import omnikryptec.animation.loaders.AnimatedModelLoader;
import omnikryptec.animation.loaders.AnimationLoader;
import omnikryptec.display.DisplayManager;
import omnikryptec.entity.Camera;
import omnikryptec.entity.Entity;
import omnikryptec.entity.EntityBuilder;
import omnikryptec.event.EventSystem;
import omnikryptec.event.EventType;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene;
import omnikryptec.settings.GameSettings;
import omnikryptec.settings.Key;
import omnikryptec.settings.KeyGroup;
import omnikryptec.settings.KeySettings;
import omnikryptec.test.saving.DataMapSerializer;
import omnikryptec.test.saving.XMLSerializer;
import omnikryptec.util.AdvancedFile;
import omnikryptec.util.InputUtil;
import omnikryptec.util.NativesLoader;
import omnikryptec.util.OSUtil;
import omnikryptec.util.RenderUtil;

/**
 * AnimationTest
 * @author Panzer1119
 */
public class AnimationTest {
    
    private static final DataMapSerializer dataMapSerializer = new DataMapSerializer();
    private static EntityBuilder entityBuilder_brunnen;
    private static Entity entity_ball;
    private static final Random random = new Random();
    private static GameSettings gameSettings;
    private static KeySettings keySettings;
    private static Scene scene = null;
    private static Camera camera = null;
    private static AnimatedModel animatedModel;
    private static Animation animation;
    private static Entity entity_test;
    private static float speedFactor = 1.0F;
    private static final AdvancedFile RES_FOLDER_1 = new AdvancedFile("res");
    private static final String MODEL_FILE = "model.dae";
    private static final String ANIM_FILE = "model.dae";
    private static final String DIFFUSE_FILE = "diffuse.png";
    private static final AdvancedFile SAVE = new AdvancedFile(OSUtil.getStandardAppDataFolder(), "saves", "save.xml");
    
    public static final void main(String[] args) {
        try {
            NativesLoader.loadNatives();
            OmniKryptecEngine.addShutdownHook(() -> {
                save();
                NativesLoader.loadNatives();
            });
            Logger.enableLoggerRedirection(true);
            Logger.setDebugMode(true);
            Logger.CONSOLE.setEnabled(true);
            Logger.showConsoleDirect();
            
            Logger.log(SAVE);
            
            gameSettings = new GameSettings("AnimationTest", 1280, 720).setAnisotropicLevel(32).setMultisamples(32).setChunkSize(400, 400, 400);
            keySettings = gameSettings.getKeySettings();
            final KeyGroup grabMouse = new KeyGroup("grabMouse", new Key("grabMouse1", Keyboard.KEY_G, true), new Key("grabMouse2", Keyboard.KEY_Y, true)).setAllKeysNeedToBeActivated(false);
            keySettings.setKey(grabMouse);
            keySettings.setKey("sprint", Keyboard.KEY_LCONTROL, true);
            keySettings.setKey("toggleWireframe", Keyboard.KEY_T, true);
            keySettings.setKey("reset", Keyboard.KEY_R, true);
            keySettings.setKey("alternativeMouseGrabbed", Keyboard.KEY_L, true);
            keySettings.setKey("lower", Keyboard.KEY_COMMA, true);
            keySettings.setKey("higher", Keyboard.KEY_PERIOD, true);
            DisplayManager.createDisplay("Animation Test", gameSettings);
            OmniKryptecEngine.instance().addAndSetScene((scene = new Scene("Test-Scene", camera = ((Camera) new Camera() {

                @Override
                public final void doLogic() {
                    float horizontalSpeed = 30.0F * 0.5F;
                    float verticalSpeed = 10.0F * 0.5F;
                    float turnSpeed = 40.0F * 0.5F;
                    if(keySettings.isPressed("sprint")) {
                        horizontalSpeed *= 10;
                        verticalSpeed *= 10;
                        turnSpeed *= 1;
                    }
                    InputUtil.doThirdPersonController(this, this, keySettings, horizontalSpeed, verticalSpeed, turnSpeed);
                }

            }.setPerspectiveProjection(75, 0.1F, 1000)))));
            if(SAVE.exists()) {
                load();
            }
            entityBuilder_brunnen = new EntityBuilder().loadModel("/omnikryptec/test/brunnen.obj").loadTexture("/omnikryptec/test/brunnen.png");
            entity_ball = entityBuilder_brunnen.create();
            animatedModel = AnimatedModelLoader.loadModel(new AdvancedFile(RES_FOLDER_1, MODEL_FILE), new AdvancedFile(RES_FOLDER_1, DIFFUSE_FILE), null);
            animation = AnimationLoader.loadAnimation(new AdvancedFile(RES_FOLDER_1, ANIM_FILE));
            entity_test = new Entity(animatedModel) {
                
                @Override
                public final void doLogic() {
                    setRelativePos(camera.getAbsolutePos().x, camera.getAbsolutePos().y, camera.getAbsolutePos().z);
                    getRelativeRotation().y = camera.getAbsoluteRotation().y;
                }
                
            };
            animatedModel.doAnimation(animation);
            OmniKryptecEngine.getInstance().getCurrentScene().addGameObject(entity_ball);
            OmniKryptecEngine.getInstance().getCurrentScene().addGameObject(entity_test);
            if(!SAVE.exists()) {
                camera.getRelativePos().y += 3;
                camera.getRelativeRotation().y = 90;
            }
            entity_ball.getRelativePos().x += 8;
            entity_ball.getRelativePos().y += 1;
            EventSystem.instance().addEventHandler((e) -> {
                input();
                logic();
                AnimatedModel.updateAllAnimatedModels();
            }, EventType.RENDER_EVENT);
            InputUtil.setCamera(camera);
            InputUtil.setLongButtonPressEnabled(true);
            OmniKryptecEngine.getInstance().startLoop(OmniKryptecEngine.ShutdownOption.JAVA);
        } catch (Exception ex) {
            Logger.logErr("Main Error: " + ex, ex);
        }
    }
    
    public static final void load() {
        dataMapSerializer.reset();
        dataMapSerializer.unserializeToDataMap(SAVE, XMLSerializer.newInstance());
        scene.fromDataMap(dataMapSerializer.getDataMaps(Scene.class).get(0));
    }
    
    public static final void save() {
        SAVE.createFile();
        final Scene scene = OmniKryptecEngine.getInstance().getCurrentScene();
        final String sceneName = OmniKryptecEngine.getInstance().getCurrentSceneName();
        dataMapSerializer.reset();
        dataMapSerializer.addObject(scene);
        dataMapSerializer.serialize(sceneName, XMLSerializer.newInstance(), SAVE);
        Logger.log(String.format("Saved Scene \"%s\" in file \"%s\"", sceneName, SAVE));
    }
    
    private static final void input() {
        if(keySettings.isLongPressed("toggleWireframe", 100, 400)) {
            RenderUtil.goWireframe(!RenderUtil.isWireframe());
        }
        if(keySettings.isLongPressed("grabMouse", 100, 400)) {
            Mouse.setGrabbed(!Mouse.isGrabbed());
        }
        if(keySettings.isPressed("reset")) {
            camera.getRelativePos().x = 0;
            camera.getRelativePos().y = 0;
            camera.getRelativePos().z = 0;
            camera.getRelativePos().y += 3;
            camera.getRelativeRotation().y = 90;
        }
        float deltaX = InputUtil.getMouseDelta().x;
        float deltaY = InputUtil.getMouseDelta().y;
        float deltaD = InputUtil.getMouseDelta().z;
        if(Mouse.isGrabbed()) {
            deltaX *= -1;
            deltaY *= -1;
            deltaD *= 1;
        }
        if(keySettings.isPressed("mouseButtonLeft") || Mouse.isGrabbed()) {
            if(keySettings.isPressed("alternativeMouseGrabbed")) {
                InputUtil.moveXZ(camera, camera, -deltaY / 15, -deltaX / 15, deltaD);
            } else {
                camera.getRelativeRotation().y -= (deltaX / 5);
                camera.getRelativeRotation().x += (deltaY / 5);
            }
        }
        float deltaSpeedFactor = 0.0F;
        if(keySettings.isPressed("lower")) {
            deltaSpeedFactor -= 0.005F;
        }
        if(keySettings.isPressed("higher")) {
            deltaSpeedFactor += 0.005F;
        }
        speedFactor += deltaSpeedFactor;
}
    
    private static final void logic() {
        animatedModel.getAnimator().setSpeedFactor(speedFactor);
        speedFactor = animatedModel.getAnimator().getSpeedFactor();
    }
    
}
