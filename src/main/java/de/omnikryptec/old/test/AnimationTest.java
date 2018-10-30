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

package de.omnikryptec.old.test;

import java.util.Random;
import java.util.zip.Deflater;

import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.graphics.display.DisplayManager;
import de.omnikryptec.libapi.glfw.WindowInfo;
import de.omnikryptec.old.animation.AnimatedModel;
import de.omnikryptec.old.animation.Animation;
import de.omnikryptec.old.animation.ColladaParser.dataStructures.AnimatedModelData;
import de.omnikryptec.old.animation.loaders.AnimatedModelLoader;
import de.omnikryptec.old.event.input.CursorType;
import de.omnikryptec.old.event.input.InputManager;
import de.omnikryptec.old.gameobject.Camera;
import de.omnikryptec.old.gameobject.Entity;
import de.omnikryptec.old.gameobject.EntityBuilder;
import de.omnikryptec.old.gameobject.GameObject;
import de.omnikryptec.old.graphics.GraphicsUtil;
import de.omnikryptec.old.main.OmniKryptecEngine;
import de.omnikryptec.old.main.Scene3D;
import de.omnikryptec.old.resource.loader.ResourceLoader;
import de.omnikryptec.old.resource.model.Model;
import de.omnikryptec.old.resource.model.TexturedModel;
import de.omnikryptec.old.resource.texture.SimpleTexture;
import de.omnikryptec.old.resource.texture.Texture;
import de.omnikryptec.old.settings.GameSettings;
import de.omnikryptec.old.settings.Key;
import de.omnikryptec.old.settings.KeyGroup;
import de.omnikryptec.old.settings.KeySettings;
import de.omnikryptec.old.test.saving.DataMapSerializer;
import de.omnikryptec.old.test.saving.XMLSerializer;
import de.omnikryptec.old.util.NativesLoader;
import de.omnikryptec.old.util.OSUtil;
import de.omnikryptec.old.util.logger.Logger;
import de.omnikryptec.old.util.profiler.LiveProfiler;

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
    private static Scene3D scene = null;
    private static Camera camera = null;
    private static AnimatedModel animatedModel;
    private static Animation animation;
    private static Entity entity_test;
    private static float speedFactor = 1.0F;
    private static final AdvancedFile RES_FOLDER_1 = new AdvancedFile(true, "", "res");
    private static final AdvancedFile MODEL_FILE = new AdvancedFile(true, RES_FOLDER_1, "model.dae");
    private static final AdvancedFile ANIM_FILE = new AdvancedFile(true, RES_FOLDER_1, "model.dae");
    private static final AdvancedFile DIFFUSE_FILE = new AdvancedFile(true, RES_FOLDER_1, "diffuse.png");
    private static final AdvancedFile SAVE = new AdvancedFile(false, OSUtil.getStandardAppDataEngineFolder(), "saves",
	    "save.xml");
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

	    gameSettings = new GameSettings().setAnisotropicLevel(32).setMultisamples(32)
		    /* .setInitialFPSCap(30) */.setChunkRenderOffsets(2, 2, 2).setLightForward(true);
	    keySettings = gameSettings.getKeySettings();
	    final KeyGroup grabMouse = new KeyGroup("grabMouse", new Key("grabMouse1", GLFW.GLFW_KEY_G, true),
		    new Key("grabMouse2", GLFW.GLFW_KEY_Y, true)).setAllKeysNeedToBeActivated(false);
	    keySettings.setKey(grabMouse);
	    keySettings.setKey("sprint", GLFW.GLFW_KEY_LEFT_CONTROL, true);
	    keySettings.setKey("toggleWireframe", GLFW.GLFW_KEY_T, true);
	    keySettings.setKey("reset", GLFW.GLFW_KEY_R, true);
	    keySettings.setKey("alternativeMouseGrabbed", GLFW.GLFW_KEY_L, true);
	    keySettings.setKey("lower", GLFW.GLFW_KEY_COMMA, true);
	    keySettings.setKey("higher", GLFW.GLFW_KEY_PERIOD, true);
	    DisplayManager.createDisplay("Animation Test", gameSettings, new WindowInfo(4, 3, false, false, 1280, 720));
	    OmniKryptecEngine.instance()
		    .addAndSetScene((scene = new Scene3D("Test-Scene", camera = ((Camera) new Camera() {

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
			    InputManager.doThirdPersonController(this, this, keySettings, horizontalSpeed,
				    verticalSpeed, turnSpeed);
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

	    // FIXME Only for testing DON'T DELETE TIHS!!! START
	    AdvancedFile res_test = new AdvancedFile(true, "", "de", "omnikryptec", "test");
	    ResourceLoader.createInstanceDefault(true, false);
	    // ResourceLoader.getInstance().stageAdvancedFiles(-1, DIFFUSE_FILE);
	    // ResourceLoader.getInstance().stageAdvancedFiles(MODEL_FILE);
	    ResourceLoader.currentInstance().stageAdvancedFiles(1, RES_FOLDER_1);
	    ResourceLoader.currentInstance().stageAdvancedFiles(new AdvancedFile(true, res_test, "brunnen.obj"));
	    ResourceLoader.currentInstance().stageAdvancedFiles(new AdvancedFile(true, res_test, "brunnen.png"));
	    ResourceLoader.currentInstance().loadStagedAdvancedFiles(true);
	    Logger.log("Local Folder: " + new AdvancedFile(false, "").getAbsoluteAdvancedFile());
	    // file = new AdvancedFile("pictures", String.format("test_%s%d.png",
	    // (withTransparency ? "withTransparency_" : ""),
	    // JBulletTest2.imagesTaken)).getAbsoluteAdvancedFile();
	    // FIXME Only for testing DON'T DELETE TIHS!!! END

	    animatedModel = AnimatedModelLoader.createModel("res:model.dae:AnimatedModel",
		    ResourceLoader.currentInstance().getResource(AnimatedModelData.class,
			    "res:model.dae:AnimatedModelData"),
		    ResourceLoader.currentInstance().getResource(Texture.class, "res:diffuse.png"), null);
	    ResourceLoader.currentInstance().addRessourceObject("res:model.dae:AnimatedModel", animatedModel);
	    ResourceLoader.currentInstance().addRessourceObject("omnikryptec:test:brunnen", new TexturedModel(
		    "omnikryptec:test:brunnen",
		    ResourceLoader.currentInstance().getResource(Model.class, "omnikryptec:test:brunnen.obj"),
		    ResourceLoader.currentInstance().getResource(SimpleTexture.class, "omnikryptec:test:brunnen.png")));

	    // entityBuilder_brunnen = new
	    // EntityBuilder().setTexturedModelName("omnikryptec:test:brunnen.png").loadModel("/de/omnikryptec/test/brunnen.obj").setTexture(ResourceLoader.getInstance().getData(SimpleTexture.class,
	    // "omnikryptec:test:brunnen.png"))/*.loadTexture("brunnen.png",
	    // "/de/omnikryptec/test/brunnen.png")*/;
	    entityBuilder_brunnen = new EntityBuilder().setTexturedModel(
		    ResourceLoader.currentInstance().getResource(TexturedModel.class, "omnikryptec:test:brunnen"));
	    entity_brunnen = entityBuilder_brunnen.create("entity_brunnen");

	    animation = ResourceLoader.currentInstance().getResource(Animation.class, "res:model.dae:Animation");
	    Logger.log("");
	    entity_test = new Entity("entity_test", animatedModel) {

		@Override
		public final void update() {
		    getTransform().setPosition(camera.getTransform().getPosition());
		    getTransform().increaseRotation(0, camera.getTransform().getEulerAngelsXYZ().y, 0);
		}

	    };
	    Logger.log("Animation HashCode: " + animation.hashCode());
	    animatedModel.doAnimation(animation);
	    if (SAVE.exists()) {
		load();
	    }
	    if (!SAVE.exists()) {
		OmniKryptecEngine.instance().getCurrent3DScene().addGameObject(entity_brunnen);
		OmniKryptecEngine.instance().getCurrent3DScene().addGameObject(entity_test);
		camera.getTransform().increasePosition(0, 3, 0);
		camera.getTransform().increaseRotation(0, 90, 0);
		entity_brunnen.getTransform().increasePosition(8, 1, 0);
	    }
	    /*
	     * //FIXME fix the EventSystem
	     * OmniKryptecEngine.instance().getEventsystem().addEventHandler((e) -> {
	     * input(); logic(); AnimatedModel.updateAllAnimatedModels(); },
	     * EventType.RENDER_FRAME_EVENT);
	     */
	    InputManager.setCamera(camera);
	    InputManager.setLongButtonPressEnabled(true);
	    OmniKryptecEngine.instance().startLoop();
	} catch (Exception ex) {
	    Logger.logErr("Main Error: " + ex, ex);
	}
    }

    public static final void load() {
	try {
	    dataMapSerializer.reset();
	    dataMapSerializer.deserializeToDataMap(SAVE.createInputStream(), XMLSerializer.newInstance());
	    // dataMapSerializer.deserializeToDataMap(new
	    // InflaterInputStream(SAVE.createInputStream()), XMLSerializer.newInstance());
	    dataMapSerializer.getClassesDataMaps().keySet().stream().filter((c) -> (c != null && c != Camera.class
		    && !c.isAnonymousClass() && GameObject.class.isAssignableFrom(c))).forEach((c) -> {
			dataMapSerializer.getClassesDataMaps().get(c).stream().forEach((dataMap) -> {
			    Logger.log(c + " creating " + dataMap);
			    try {
				c.getMethod("newInstanceFromDataMap", dataMap.getClass()).invoke(c.newInstance(),
					dataMap);
			    } catch (Exception ex) {
				Logger.logErr("Error while creating \"" + c.getSimpleName() + "\": " + ex, ex);
			    }
			});
		    });
	    scene.fromDataMap(dataMapSerializer.getDataMaps(Scene3D.class).get(0));
	    Logger.log(String.format("Loaded Scene \"%s\" from file \"%s\"", scene.getName(), SAVE));
	} catch (Exception ex) {
	    Logger.logErr("Error while loading save: " + ex, ex);
	}
    }

    public static final void save() {
	try {
	    SAVE.createAdvancedFile();
	    final String sceneName = scene.getName();
	    dataMapSerializer.reset();
	    GameObject.gameObjects.stream().filter((gameObject) -> (gameObject.getName() != null
		    && !gameObject.getName().isEmpty() && gameObject.getClass() != Camera.class))
		    .forEach((gameObject) -> {
			dataMapSerializer.addObject(gameObject);
		    });
	    dataMapSerializer.addObject(scene);
	    dataMapSerializer.serialize(sceneName, XMLSerializer.newInstance(), SAVE.createOutputstream(false));
	    // dataMapSerializer.serialize(sceneName, XMLSerializer.newInstance(), new
	    // DeflaterOutputStream(SAVE.createOutputstream(false), deflater));
	    Logger.log(String.format("Saved Scene \"%s\" in file \"%s\"", sceneName, SAVE));
	} catch (Exception ex) {
	    Logger.logErr("Error while saving save: " + ex, ex);
	}
    }

    private static final void input() {
	if (keySettings.isLongPressed("toggleWireframe", 100, 400)) {
	    GraphicsUtil.goWireframe(!GraphicsUtil.isWireframe());
	}
	if (keySettings.isLongPressed("grabMouse", 100, 400)) {
	    InputManager.setCursorType(
		    ((InputManager.getCursorType() == CursorType.DISABLED) ? CursorType.NORMAL : CursorType.DISABLED));
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
