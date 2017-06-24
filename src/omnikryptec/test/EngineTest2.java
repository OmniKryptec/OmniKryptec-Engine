package omnikryptec.test;

import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector3f;

import omnikryptec.animation.ColladaParser.colladaLoader.ColladaLoader;
import omnikryptec.display.DisplayManager;
import omnikryptec.display.OpenGLInfo;
import omnikryptec.entity.Camera;
import omnikryptec.entity.Entity;
import omnikryptec.entity.Entity.RenderType;
import omnikryptec.entity.Light;
import omnikryptec.event.Event;
import omnikryptec.event.EventSystem;
import omnikryptec.event.EventType;
import omnikryptec.event.IEventHandler;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.OmniKryptecEngine.ShutdownOption;
import omnikryptec.main.Scene;
import omnikryptec.model.Model;
import omnikryptec.model.TexturedModel;
import omnikryptec.objConverter.ObjLoader;
import omnikryptec.particles.ParticleSystem;
import omnikryptec.particles.ParticleTexture;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.settings.GameSettings;
import omnikryptec.texture.AtlasTexture;
import omnikryptec.texture.SimpleAnimation;
import omnikryptec.texture.SimpleTexture;
import omnikryptec.util.AdvancedFile;
import omnikryptec.util.InputUtil;
import omnikryptec.util.NativesLoader;

/**
 *
 * @author Panzer1119
 */
public class EngineTest2 implements IEventHandler {

	public static void main(String[] args) {
		try {
			NativesLoader.loadNatives();
			OmniKryptecEngine.addShutdownHook(() -> NativesLoader.unloadNatives());
			Logger.enableLoggerRedirection(true);
			Logger.setDebugMode(true);
			Logger.CONSOLE.setExitWhenLastOne(true);
			Logger.showConsoleDirect();

			DisplayManager.createDisplay("Test 2",
					new GameSettings("EngineTest2", 1280, 720).setAnisotropicLevel(32).setMultisamples(32)
							.setInitialFPSCap(-1).setChunkRenderOffsets(10, 10, 10).setLightForward(true),
					new OpenGLInfo(3, 3, new PixelFormat()));
			// PostProcessing.instance().addStage(new
			// DeferredLightStage(DeferredLightPrepare.ATT_LIGHT_PREPARE,
			// DeferredLightPrepare.DEFAULT_LIGHT_PREPARE));
			// PostProcessing.instance().addStage(new BloomStage(new
			// CompleteGaussianBlurStage(true, 0.6f, 0.6f), new Vector4f(1, 0,
			// 0, 0), new Vector2f(1, 6)));
			// PostProcessing.instance().addStage(new
			// FogStage().setDensity(0.25f));
			// PostProcessing.instance().addStage(new
			// CompleteGaussianBlurStage(false,0.1f,0.1f));
			// PostProcessing.instance().addStage(new ColorSpaceStage(16,4,4));
			// PostProcessing.instance().addStage(new
			// CompleteGaussianBlurStage(true,0.5f,0.5f));
			// PostProcessing.instance().addStage(new
			// ContrastchangeStage(0.75f));

			// PostProcessing.instance().addStage(new BrightnessfilterStage(new
			// Vector4f(0, 0, 0, 0)));
			// RenderUtil.goWireframe(true);
			// PostProcessing.instance().setEnabled(false);
			// PostProcessing.instance().addStage(new
			// FogStage().setDensity(0.05f).setFog(0, 0.5f, 0,
			// 0.8f).setGradient(2));
			// PostProcessing.instance().addStage(new
			// CompleteGaussianBlurStage(false, 0.6f, 0.6f));
			// PostProcessing.instance().addStage(new
			// CompleteGaussianBlurStage(false, 0.3f, 0.3f));
			// PostProcessing.instance().addStage(new
			// CompleteGaussianBlurStage(false, 0.1f, 0.1f));
			// PostProcessing.instance().addStage(new
			// CompleteGaussianBlurStage(false, 0.05f, 0.05f));

			// PostProcessing.instance().addStage(new DebugRenderer());
			AdvancedFile res = new AdvancedFile("res");
			SimpleTexture jd = SimpleTexture.newTexture(new AdvancedFile(res, "jd.png"));
			SimpleTexture js = SimpleTexture.newTexture(new AdvancedFile(res, "js.png"));
			SimpleTexture jn = SimpleTexture.newTexture(new AdvancedFile(res, "jn.png"));

			EventSystem.instance().addEventHandler(new EngineTest2(), EventType.RENDER_EVENT);
			Model brunnen = new Model(
					ObjLoader.loadOBJ(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen.obj")));
			// Model brunnen = ModelUtil.generateQuad();
			SimpleTexture brunnent = SimpleTexture
					.newTextureb(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen.png")).create();
			SimpleTexture brunnen_norm = SimpleTexture
					.newTextureb(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen_normal.png")).create();
			AtlasTexture rmvp = new AtlasTexture(brunnent, 0.25f, 0.25f, 0.5f, 0.5f);
			TexturedModel tm = new TexturedModel(brunnen, brunnent);
			tm.getMaterial().setNormalmap(brunnen_norm);
			tm.getMaterial().setHasTransparency(true);
			tm.getMaterial().setReflectivity(0.5f).setShineDamper(10)
					.setRenderer(RendererRegistration.DEF_FORWARD_ENTITY_RENDERER);
			OmniKryptecEngine.instance().addAndSetScene(new Scene("test", (Camera) new Camera() {

				@Override
				public void doLogic() {
					// setRelativePos(getRelativePos().x, getRelativePos().y,
					// getRelativePos().z + 0.1f *
					// DisplayManager.instance().getDeltaTime());
					doCameraLogic(this);
				}

			}.setPerspectiveProjection(90, 0.1f, 300).setRelativePos(0, 0, 0)).setAmbientColor(0,0,0));
			Model pine = new Model(
					ObjLoader.loadOBJ(EngineTest.class.getResourceAsStream("/omnikryptec/test/pine.obj")));
			Model bauer = new Model(
					ColladaLoader.loadColladaModel(new AdvancedFile("res", "model.dae"), 50).getMeshData());
			SimpleTexture bauert = SimpleTexture.newTexture("/res/diffuse.png");
			SimpleTexture pinet = SimpleTexture
					.newTextureb(EngineTest.class.getResourceAsStream("/omnikryptec/test/pine2.png")).create();

			SimpleAnimation animation = new SimpleAnimation(2, brunnent, pinet);
			SimpleTexture pine_normal = SimpleTexture.newTexture("/omnikryptec/test/pine2_normal.png");

			TexturedModel ptm = new TexturedModel(pine,pinet);
			ptm.getMaterial().setNormalmap(pine_normal);
			ptm.getMaterial().setHasTransparency(true).setRenderer(RendererRegistration.DEF_FORWARD_ENTITY_RENDERER);
			ptm.getMaterial().setReflectivity(0.1f).setShineDamper(10).setExtraInfoVec(new Vector3f(1, 1, 0));
			Random r = new Random();
			for (int i = 0; i < 100; i++) {
				Entity e = new Entity(ptm) {
					@Override
					public void doLogic() {
						// setColor(r.nextFloat(), r.nextFloat(), r.nextFloat(),
						// r.nextFloat());
						// InputUtil.doFirstPersonController(this,
						// DisplayManager.instance().getSettings().getKeySettings(),
						// 1, 1, 1);
						// increaseRelativeRot(0, 1, 0);
					}
				}.setScale(new Vector3f(3, 3, 3));
				// e.setColor(r.nextFloat(), r.nextFloat(), r.nextFloat(), 1);
				e.setRelativePos(r.nextInt(100) - 50, r.nextInt(100) - 50, r.nextInt(100) - 50);
				OmniKryptecEngine.instance().getCurrentScene().addGameObject(e);
			}
			// ParticleSystem - unoptimisiert 70FPS - optimisiert 83 FPS
			system = new ParticleSystem(0, 0, 0,
					new ParticleTexture(SimpleTexture.newTexture("/omnikryptec/test/cosmic.png"), 4, false), 20f, 2.5f,
					new Vector3f(0, 0, 0), 2f, 1.25f, RenderType.ALWAYS);
			// system.setTimemultiplier(10);
			OmniKryptecEngine.instance().getCurrentScene().addGameObject(system);
			OmniKryptecEngine.instance().getCurrentScene()
					.addGameObject(new Light().setAttenuation(0,0.001f, 0).setColor(1, 1, 1).setRelativePos(0, 0, 0));
			// ent.setParent(OmniKryptecEngine.instance().getCurrentScene().getCamera());
			// OmniKryptecEngine.instance().getCurrentScene().addGameObject(new
			// Light().setColor(1, 1, 0).setRadius(100));
			// OmniKryptecEngine.instance().getCurrentScene().addGameObject(new
			// Light().setColor(1, 0, 1).setRadius(100).setRelativePos(50, 50,
			// 50));
			// OmniKryptecEngine.instance().getCurrentScene().addGameObject(new
			// Light().setColor(1, 1,
			// 1).setRadius(-1).setShader(LightPrepare.DEFAULT_LIGHT_PREPARE));
			// .instance().getCurrentScene().addGameObject(new
			// Light().setColor(0, 0, 1).setRadius(100).setRelativePos(50, 50,
			// 0));

			OmniKryptecEngine.instance().startLoop(ShutdownOption.JAVA);
		} catch (Exception ex) {
			Logger.logErr("Error: " + ex, ex);
		}
	}

	private static float v = 20;

	private static void doCameraLogic(Camera camera) {
		// v += DisplayManager.instance().getDeltaTime()*30;
		InputUtil.doFirstPersonController(camera, DisplayManager.instance().getSettings().getKeySettings(), v, v, 40,
				false);
		// camera.setPerspectiveProjection(Maths.alterFOV(10, 179f, v, 1000),
		// 1000, 0.001f);
		Logger.CONSOLE.setTitle(camera.toString());
	}

	private static ParticleSystem system;

	@Override
	public void onEvent(Event ev) {
		// system.generateParticles(1);
		Display.setTitle("FPS: " + DisplayManager.instance().getFPS());
		// System.out.println(DisplayManager.instance().getFPS());
		// System.out.println(DisplayManager.instance().getDeltaTime());
	}

}
