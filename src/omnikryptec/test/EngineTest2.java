package omnikryptec.test;

import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import omnikryptec.display.DisplayManager;
import omnikryptec.display.OpenGLInfo;
import omnikryptec.entity.Camera;
import omnikryptec.entity.Entity;
import omnikryptec.entity.Entity.RenderType;
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
import omnikryptec.particles.Particle;
import omnikryptec.particles.ParticleMaster;
import omnikryptec.particles.ParticleSystem;
import omnikryptec.particles.ParticleTexture;
import omnikryptec.postprocessing.DebugRenderer;
import omnikryptec.postprocessing.PostProcessing;
import omnikryptec.ppstages.BloomStage;
import omnikryptec.ppstages.ColorSpaceStage;
import omnikryptec.ppstages.CompleteGaussianBlurStage;
import omnikryptec.ppstages.ContrastchangeStage;
import omnikryptec.settings.GameSettings;
import omnikryptec.texture.AtlasTexture;
import omnikryptec.texture.SimpleAnimation;
import omnikryptec.texture.SimpleTexture;
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

			DisplayManager
					.createDisplay("Test 2",
							new GameSettings("EngineTest2", 1280, 720).setAnisotropicLevel(32).setMultisamples(32)
									.setInitialFPSCap(-1).setChunkOffsets(10, 10, 10),
							new OpenGLInfo(3, 3, new PixelFormat()));
			 PostProcessing.instance().addStage(new BloomStage(new CompleteGaussianBlurStage(true, 0.6f, 0.6f), new Vector4f(1, 0, 0, 0), new Vector2f(1, 6)));
			// PostProcessing.instance().addStage(new
			// LightStage(LightPrepare.ATT_LIGHT_PREPARE,
			// LightPrepare.DEFAULT_LIGHT_PREPARE));
//			 PostProcessing.instance().addStage(new
//			 CompleteGaussianBlurStage(false,0.1f,0.1f));
//			 PostProcessing.instance().addStage(new ColorSpaceStage(16,4,4));
//			 PostProcessing.instance().addStage(new
//			 CompleteGaussianBlurStage(true,0.5f,0.5f));
//			PostProcessing.instance().addStage(new
//			ContrastchangeStage(0.75f));

			 //PostProcessing.instance().addStage(new BrightnessfilterStage(new Vector4f(0, 0, 0, 0)));
			// RenderUtil.goWireframe(true);
			// PostProcessing.instance().setEnabled(false);
//			 PostProcessing.instance().addStage(new
//			 FogStage().setDensity(0.05f).setFog(0, 0.5f, 0,
//			 0.8f).setGradient(2));
//			 PostProcessing.instance().addStage(new
//			 CompleteGaussianBlurStage(false, 0.6f, 0.6f));
//			 PostProcessing.instance().addStage(new
//			 CompleteGaussianBlurStage(false, 0.3f, 0.3f));
//			 PostProcessing.instance().addStage(new
//			 CompleteGaussianBlurStage(false, 0.1f, 0.1f));
//			 PostProcessing.instance().addStage(new
//			 CompleteGaussianBlurStage(false, 0.05f, 0.05f));

			//PostProcessing.instance().addStage(new DebugRenderer());
			EventSystem.instance().addEventHandler(new EngineTest2(), EventType.RENDER_EVENT);
			Model brunnen = new Model(
					ObjLoader.loadNMOBJ(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen.obj")));
			// Model brunnen = ModelUtil.generateQuad();
			SimpleTexture brunnent = SimpleTexture
					.newTexture(EngineTest.class.getResourceAsStream("/omnikryptec/test/brunnen.png")).create();
			AtlasTexture rmvp = new AtlasTexture(brunnent, 0.25f, 0.25f, 0.5f, 0.5f);
			TexturedModel tm = new TexturedModel(brunnen, rmvp);
			tm.getMaterial().setHasTransparency(true);
			tm.getMaterial().setReflectivity(1);
			OmniKryptecEngine.instance().addAndSetScene("test", new Scene((Camera) new Camera() {

				@Override
				public void doLogic() {
					// setRelativePos(getRelativePos().x, getRelativePos().y,
					// getRelativePos().z + 0.1f *
					// DisplayManager.instance().getDeltaTime());
					doCameraLogic(this);
				}

			}.setPerspectiveProjection(90, 0.1f, 300).setRelativePos(0, 0, 0)));
			Model pine = new Model(
					ObjLoader.loadNMOBJ(EngineTest.class.getResourceAsStream("/omnikryptec/test/pine.obj")));
			SimpleTexture pinet = SimpleTexture
					.newTexture(EngineTest.class.getResourceAsStream("/omnikryptec/test/pine2.png")).create();

			SimpleAnimation animation = new SimpleAnimation(2, brunnent, pinet);

			TexturedModel ptm = new TexturedModel(pine, pinet);
			ptm.getMaterial().setHasTransparency(true);
			ptm.getMaterial().setReflectivity(0.1f).setShineDamper(0.01f).setExtraInfoVec(new Vector4f(1, 1, 0, 0));
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
				};
				// e.setColor(r.nextFloat(), r.nextFloat(), r.nextFloat(), 1);
				e.setRelativePos(r.nextInt(100) - 50, r.nextInt(100) - 50, r.nextInt(100) - 50);
				OmniKryptecEngine.instance().getCurrentScene().addGameObject(e);
			}
			//ParticleSystem - unoptimisiert 70FPS - optimisiert 83 FPS
			system = new ParticleSystem(0,0,0, new ParticleTexture(SimpleTexture.newTexture(Utils.class.getResourceAsStream("/omnikryptec/test/cosmic.png")).create(), 4, false), 50, 5, new Vector3f(0, 0, 0), 4f, 1.25f, RenderType.ALWAYS);
			OmniKryptecEngine.instance().getCurrentScene().addGameObject(system);
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
		//system.generateParticles(1);
		Display.setTitle("FPS: "+DisplayManager.instance().getFPS());
		// System.out.println(DisplayManager.instance().getFPS());
		// System.out.println(DisplayManager.instance().getDeltaTime());
	}

}
