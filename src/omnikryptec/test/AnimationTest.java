package omnikryptec.test;

import java.util.Random;
import omnikryptec.animation.AnimatedModel;
import omnikryptec.animation.Animation;
import omnikryptec.animation.loaders.AnimatedModelLoader;
import omnikryptec.animation.loaders.AnimationLoader;
import omnikryptec.animation.renderer.AnimatedModelRenderer;
import omnikryptec.display.DisplayManager;
import omnikryptec.entity.Camera;
import omnikryptec.entity.Entity;
import omnikryptec.entity.EntityBuilder;
import omnikryptec.event.EventSystem;
import omnikryptec.event.EventType;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene;
import omnikryptec.model.Material;
import omnikryptec.settings.GameSettings;
import omnikryptec.settings.Key;
import omnikryptec.settings.KeyGroup;
import omnikryptec.settings.KeySettings;
import omnikryptec.util.InputUtil;
import omnikryptec.util.MyFile;
import omnikryptec.util.NativesLoader;
import omnikryptec.util.RenderUtil;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * AnimationTest
 * @author Panzer1119
 */
public class AnimationTest {
    
    private static AnimatedModelRenderer renderer_animation;
    private static EntityBuilder entityBuilder_brunnen;
    private static Entity entity_ball;
    private static final Random random = new Random();
    private static GameSettings gameSettings;
    private static KeySettings keySettings;
    private static Camera camera;
    private static AnimatedModel animatedModel;
    private static Animation animation;
    private static Entity entity_test;
    
    public static final void main(String[] args) {
        try {
            NativesLoader.loadNatives();
            OmniKryptecEngine.addShutdownHook(() -> NativesLoader.loadNatives());
            Logger.enableLoggerRedirection(true);
            Logger.setDebugMode(true);
            Logger.CONSOLE.setEnabled(true);
            Logger.showConsoleDirect();
            
            final MyFile RES_FOLDER = new MyFile("res");
            final String MODEL_FILE = "model.dae";
            final String ANIM_FILE = "model.dae";
            final String DIFFUSE_FILE = "diffuse.png";
            
            gameSettings = new GameSettings("AnimationTest", 1280, 720).setAnisotropicLevel(32).setMultisamples(32).setChunkSize(400, 400, 400);
            keySettings = gameSettings.getKeySettings();
            final KeyGroup grabMouse = new KeyGroup("grabMouse", new Key("grabMouse1", Keyboard.KEY_G, true), new Key("grabMouse2", Keyboard.KEY_Y, true)).setAllKeysNeedToBeActivated(false);
            keySettings.setKey(grabMouse);
            keySettings.setKey("sprint", Keyboard.KEY_LCONTROL, true);
            keySettings.setKey("toggleWireframe", Keyboard.KEY_T, true);
            keySettings.setKey("reset", Keyboard.KEY_R, true);
            keySettings.setKey("alternativeMouseGrabbed", Keyboard.KEY_L, true);
            DisplayManager.createDisplay("Animation Test", gameSettings);
            OmniKryptecEngine.instance().addAndSetScene("Test-Scene", new Scene(camera = ((Camera) new Camera() {
                
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
                
            }.setPerspectiveProjection(75, 0.1F, 1000))));
            entityBuilder_brunnen = new EntityBuilder().loadModel("/omnikryptec/test/brunnen.obj").loadTexture("/omnikryptec/test/brunnen.png");
            entity_ball = entityBuilder_brunnen.create();
            renderer_animation = new AnimatedModelRenderer();
            animatedModel = AnimatedModelLoader.loadEntity(new MyFile(RES_FOLDER, MODEL_FILE), new MyFile(RES_FOLDER, DIFFUSE_FILE));
            animatedModel.getMaterial().setRenderer(renderer_animation);
            animation = AnimationLoader.loadAnimation(new MyFile(RES_FOLDER, ANIM_FILE));
            entity_test = new Entity(animatedModel);
            animatedModel.doAnimation(animation);
            OmniKryptecEngine.getInstance().getCurrentScene().addGameObject(entity_ball);
            OmniKryptecEngine.getInstance().getCurrentScene().addGameObject(entity_test);
            camera.getRelativePos().y += 3;
            camera.getRelativeRotation().y = 90;
            entity_ball.getRelativePos().x += 8;
            entity_ball.getRelativePos().y += 1;
            EventSystem.instance().addEventHandler((e) -> {
                input();
                AnimatedModel.updateAllAnimatedModels();
            }, EventType.RENDER_EVENT);
            InputUtil.setCamera(camera);
            InputUtil.setLongButtonPressEnabled(true);
            OmniKryptecEngine.getInstance().startLoop(OmniKryptecEngine.ShutdownOption.JAVA);
        } catch (Exception ex) {
            Logger.logErr("Main Error: " + ex, ex);
        }
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
    }
    
}
