package omnikryptec.test;

import java.util.Random;
import java.util.zip.Deflater;

import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import omnikryptec.animation.AnimatedModel;
import omnikryptec.animation.Animation;
import omnikryptec.animation.ColladaParser.dataStructures.AnimatedModelData;
import omnikryptec.animation.loaders.AnimatedModelLoader;
import omnikryptec.display.DisplayManager;
import omnikryptec.display.GLFWInfo;
import omnikryptec.event.event.EventSystem;
import omnikryptec.event.event.EventType;
import omnikryptec.event.input.CursorType;
import omnikryptec.event.input.InputManager;
import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.Entity;
import omnikryptec.gameobject.EntityBuilder;
import omnikryptec.gameobject.GameObject;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene;
import omnikryptec.resource.loader.DefaultAnimatedModelDataLoader;
import omnikryptec.resource.loader.DefaultAnimationLoader;
import omnikryptec.resource.loader.DefaultModelLoader;
import omnikryptec.resource.loader.DefaultTextureLoader;
import omnikryptec.resource.loader.ResourceLoader;
import omnikryptec.resource.model.Model;
import omnikryptec.resource.model.TexturedModel;
import omnikryptec.resource.texture.SimpleTexture;
import omnikryptec.resource.texture.Texture;
import omnikryptec.settings.GameSettings;
import omnikryptec.settings.Key;
import omnikryptec.settings.KeyGroup;
import omnikryptec.settings.KeySettings;
import omnikryptec.test.saving.DataMapSerializer;
import omnikryptec.test.saving.XMLSerializer;
import omnikryptec.util.AdvancedFile;
import omnikryptec.util.NativesLoader;
import omnikryptec.util.OSUtil;
import omnikryptec.util.RenderUtil;
import omnikryptec.util.logger.Logger;
import omnikryptec.util.profiler.LiveProfiler;

/**
 * AnimationTest
 *
 * @author Panzer1119
 */
public class AnimationTest {

    private static final DataMapSerializer dataMapSerializer = new DataMapSerializer();
    private static EntityBuilder entityBuilder_brunnen;
    private static Entity entity_brunnen;
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
    private static final AdvancedFile MODEL_FILE = new AdvancedFile(RES_FOLDER_1, "model.dae");
    private static final AdvancedFile ANIM_FILE = new AdvancedFile(RES_FOLDER_1, "model.dae");
    private static final AdvancedFile DIFFUSE_FILE = new AdvancedFile(RES_FOLDER_1, "diffuse.png");
    private static final AdvancedFile SAVE = new AdvancedFile(OSUtil.getStandardAppDataEngineFolder(), "saves", "save.xml");
    private static final Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);

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

            new Thread(() -> {
                LiveProfiler liveProfiler = new LiveProfiler(1000, 1000);
                liveProfiler.setLastSeconds(5);
                liveProfiler.startTimer(100);
            }).start();

            gameSettings = new GameSettings().setAnisotropicLevel(32).setMultisamples(32).setInitialFPSCap(30).setChunkRenderOffsets(2, 2, 2).setLightForward(true);
            keySettings = gameSettings.getKeySettings();
            final KeyGroup grabMouse = new KeyGroup("grabMouse", new Key("grabMouse1", GLFW.GLFW_KEY_G, true), new Key("grabMouse2", GLFW.GLFW_KEY_Y, true)).setAllKeysNeedToBeActivated(false);
            keySettings.setKey(grabMouse);
            keySettings.setKey("sprint", GLFW.GLFW_KEY_LEFT_CONTROL, true);
            keySettings.setKey("toggleWireframe", GLFW.GLFW_KEY_T, true);
            keySettings.setKey("reset", GLFW.GLFW_KEY_R, true);
            keySettings.setKey("alternativeMouseGrabbed", GLFW.GLFW_KEY_L, true);
            keySettings.setKey("lower", GLFW.GLFW_KEY_COMMA, true);
            keySettings.setKey("higher", GLFW.GLFW_KEY_PERIOD, true);
            DisplayManager.createDisplay("Animation Test", gameSettings, new GLFWInfo(4, 3, false, false, 1280, 720));
            OmniKryptecEngine.instance().addAndSetScene((scene = new Scene("Test-Scene", camera = ((Camera) new Camera() {

                @Override
                public final void update() {
                    float horizontalSpeed = 30.0F * 0.5F;
                    float verticalSpeed = 10.0F * 0.5F;
                    float turnSpeed = 40.0F * 0.5F;
                    if (keySettings.isPressed("sprint")) {
                        horizontalSpeed *= 10;
                        verticalSpeed *= 10;
                        turnSpeed *= 1;
                    }
                    InputManager.doThirdPersonController(this, this, keySettings, horizontalSpeed, verticalSpeed, turnSpeed);
                    if (true) {
                        return;
                    }
                    final Vector4f camera_ray = getViewMatrix().transform(new Vector4f(0, 0, 0, 0));
                    final Quaternionf absrot = getTransform().getRotation(true);
                    final Matrix3f view = new Matrix3f();
                    view.rotate(absrot);

                    Logger.log("camera_ray == " + camera_ray);
                }

            }.setPerspectiveProjection(75, 0.1F, 1000)))));

            //FIXME Only for testing DON'T DELETE TIHS!!! START
            AdvancedFile res_test = new AdvancedFile("omnikryptec", "test");
            ResourceLoader.getInstance().addLoader(new DefaultModelLoader());
            ResourceLoader.getInstance().addLoader(new DefaultTextureLoader());
            ResourceLoader.getInstance().addLoader(new DefaultAnimationLoader());
            ResourceLoader.getInstance().addLoader(new DefaultAnimatedModelDataLoader());
            //ResourceLoader.getInstance().stageAdvancedFiles(-1, DIFFUSE_FILE);
            //ResourceLoader.getInstance().stageAdvancedFiles(MODEL_FILE);
            ResourceLoader.getInstance().stageAdvancedFiles(1, RES_FOLDER_1);
            ResourceLoader.getInstance().stageAdvancedFiles(new AdvancedFile(res_test, "brunnen.obj"));
            ResourceLoader.getInstance().stageAdvancedFiles(new AdvancedFile(res_test, "brunnen.png"));
            ResourceLoader.getInstance().loadStagedAdvancedFiles(true);
            Logger.log("Local Folder: " + new AdvancedFile("").getAbsoluteAdvancedFile());
            //file = new AdvancedFile("pictures", String.format("test_%s%d.png", (withTransparency ? "withTransparency_" : ""), JBulletTest2.imagesTaken)).getAbsoluteAdvancedFile();
            //FIXME Only for testing DON'T DELETE TIHS!!! END

            animatedModel = AnimatedModelLoader.createModel("res:model.dae:AnimatedModel", ResourceLoader.getInstance().getData(AnimatedModelData.class, "res:model.dae:AnimatedModelData"), ResourceLoader.getInstance().getData(Texture.class, "res:diffuse.png"), null);
            ResourceLoader.getInstance().addRessourceObject("res:model.dae:AnimatedModel", animatedModel);
            ResourceLoader.getInstance().addRessourceObject("omnikryptec:test:brunnen", new TexturedModel("omnikryptec:test:brunnen", ResourceLoader.getInstance().getData(Model.class, "omnikryptec:test:brunnen.obj"), ResourceLoader.getInstance().getData(SimpleTexture.class, "omnikryptec:test:brunnen.png")));

            //entityBuilder_brunnen = new EntityBuilder().setTexturedModelName("omnikryptec:test:brunnen.png").loadModel("/omnikryptec/test/brunnen.obj").setTexture(ResourceLoader.getInstance().getData(SimpleTexture.class, "omnikryptec:test:brunnen.png"))/*.loadTexture("brunnen.png", "/omnikryptec/test/brunnen.png")*/;
            entityBuilder_brunnen = new EntityBuilder().setTexturedModel(ResourceLoader.getInstance().getData(TexturedModel.class, "omnikryptec:test:brunnen"));
            entity_brunnen = entityBuilder_brunnen.create("entity_brunnen");

            animation = ResourceLoader.getInstance().getData(Animation.class, "res:model.dae:Animation");
            Logger.log("");
            entity_test = new Entity("entity_test", animatedModel) {

                @Override
                public final void update() {
                    getTransform().setPosition(camera.getTransform().getPosition());
                    getTransform().increaseRotation(0, camera.getTransform().rotation.y, 0);
                }

            };
            Logger.log("Animation HashCode: " + animation.hashCode());
            animatedModel.doAnimation(animation);
            if (SAVE.exists()) {
                load();
            }
            if (!SAVE.exists()) {
                OmniKryptecEngine.rawInstance().getCurrentScene().addGameObject(entity_brunnen);
                OmniKryptecEngine.rawInstance().getCurrentScene().addGameObject(entity_test);
                camera.getTransform().increasePosition(0, 3, 0);
                camera.getTransform().increaseRotation(0, 90, 0);
                entity_brunnen.getTransform().increasePosition(8, 1, 0);
            }
            EventSystem.instance().addEventHandler((e) -> {
                input();
                logic();
                AnimatedModel.updateAllAnimatedModels();
            }, EventType.RENDER_FRAME_EVENT);
            InputManager.setCamera(camera);
            InputManager.setLongButtonPressEnabled(true);
            OmniKryptecEngine.rawInstance().startLoop(OmniKryptecEngine.ShutdownOption.JAVA);
        } catch (Exception ex) {
            Logger.logErr("Main Error: " + ex, ex);
        }
    }

    public static final void load() {
        try {
            dataMapSerializer.reset();
            dataMapSerializer.deserializeToDataMap(SAVE.createInputStream(), XMLSerializer.newInstance());
            //dataMapSerializer.deserializeToDataMap(new InflaterInputStream(SAVE.createInputStream()), XMLSerializer.newInstance());
            dataMapSerializer.getClassesDataMaps().keySet().stream().filter((c) -> (c != null && c != Camera.class && !c.isAnonymousClass() && GameObject.class.isAssignableFrom(c))).forEach((c) -> {
                dataMapSerializer.getClassesDataMaps().get(c).stream().forEach((dataMap) -> {
                    Logger.log(c + " creating " + dataMap);
                    try {
                        c.getMethod("newInstanceFromDataMap", dataMap.getClass()).invoke(c.newInstance(), dataMap);
                    } catch (Exception ex) {
                        Logger.logErr("Error while creating \"" + c.getSimpleName() + "\": " + ex, ex);
                    }
                });
            });
            scene.fromDataMap(dataMapSerializer.getDataMaps(Scene.class).get(0));
            Logger.log(String.format("Loaded Scene \"%s\" from file \"%s\"", scene.getName(), SAVE));
        } catch (Exception ex) {
            Logger.logErr("Error while loading save: " + ex, ex);
        }
    }

    public static final void save() {
        try {
            SAVE.createFile();
            final String sceneName = scene.getName();
            dataMapSerializer.reset();
            GameObject.gameObjects.stream().filter((gameObject) -> (gameObject.getName() != null && !gameObject.getName().isEmpty() && gameObject.getClass() != Camera.class)).forEach((gameObject) -> {
                dataMapSerializer.addObject(gameObject);
            });
            dataMapSerializer.addObject(scene);
            dataMapSerializer.serialize(sceneName, XMLSerializer.newInstance(), SAVE.createOutputstream(false));
            //dataMapSerializer.serialize(sceneName, XMLSerializer.newInstance(), new DeflaterOutputStream(SAVE.createOutputstream(false), deflater));
            Logger.log(String.format("Saved Scene \"%s\" in file \"%s\"", sceneName, SAVE));
        } catch (Exception ex) {
            Logger.logErr("Error while saving save: " + ex, ex);
        }
    }

    private static final void input() {
        if (keySettings.isLongPressed("toggleWireframe", 100, 400)) {
            RenderUtil.goWireframe(!RenderUtil.isWireframe());
        }
        if (keySettings.isLongPressed("grabMouse", 100, 400)) {
            InputManager.setCursorType(((InputManager.getCursorType() == CursorType.DISABLED) ? CursorType.NORMAL : CursorType.DISABLED));
        }
        if (keySettings.isPressed("reset")) {
            camera.getTransform().setPosition(0, 0, 0);
            camera.getTransform().increasePosition(0, 3, 0);
            camera.getTransform().increaseRotation(0, 90, 0);
        }
        float deltaX = InputManager.getMouseDelta().x;
        float deltaY = InputManager.getMouseDelta().y;
        float deltaD = InputManager.getMouseDelta().z;
        if (InputManager.getCursorType() == CursorType.DISABLED) {
            deltaX *= -1;
            deltaY *= 1;
            deltaD *= 1;
        }
        if (keySettings.isPressed("mouseButtonLeft") || (InputManager.getCursorType() == CursorType.DISABLED)) {
            if (keySettings.isPressed("alternativeMouseGrabbed")) {
                InputManager.moveXZ(camera, camera, -deltaY / 15, -deltaX / 15, deltaD);
            } else {
                camera.getTransform().increaseRotation((deltaY / 5), -(deltaX / 5), 0);
            }
        }
        float deltaSpeedFactor = 0.0F;
        if (keySettings.isPressed("lower")) {
            deltaSpeedFactor -= 0.005F;
        }
        if (keySettings.isPressed("higher")) {
            deltaSpeedFactor += 0.005F;
        }
        speedFactor += deltaSpeedFactor;
    }

    private static final void logic() {
        animatedModel.getAnimator().setSpeedFactor(speedFactor);
        speedFactor = animatedModel.getAnimator().getSpeedFactor();
        swapTexture();
    }

    private static final void swapTexture() {

    }

}
