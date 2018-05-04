package omnikryptec.main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL30;

import de.codemakers.logger.ILogger;
import omnikryptec.display.Display;
import omnikryptec.display.DisplayManager;
import omnikryptec.event.eventV2.EventBus;
import omnikryptec.event.eventV2.engineevents.CleanupEvent;
import omnikryptec.event.eventV2.engineevents.ErrorEvent;
import omnikryptec.postprocessing.main.FrameBufferObject;
import omnikryptec.postprocessing.main.PostProcessing;
import omnikryptec.postprocessing.main.RenderTarget;
import omnikryptec.renderer.d3.Query;
import omnikryptec.renderer.d3.RenderChunk3D;
import omnikryptec.renderer.d3.RendererRegistration;
import omnikryptec.resource.model.VertexArrayObject;
import omnikryptec.resource.model.VertexBufferObject;
import omnikryptec.resource.texture.SimpleTexture;
import omnikryptec.settings.GameSettings;
import omnikryptec.shader.base.Shader;
import omnikryptec.util.Color;
import omnikryptec.util.EnumCollection.DepthbufferType;
import omnikryptec.util.EnumCollection.GameLoopShutdownOption;
import omnikryptec.util.EnumCollection.GameState;
import omnikryptec.util.error.ErrorObject;
import omnikryptec.util.error.OmnikryptecError;
import omnikryptec.util.logger.Commands;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;
import omnikryptec.util.profiler.Profilable;
import omnikryptec.util.profiler.ProfileContainer;
import omnikryptec.util.profiler.Profiler;

/**
 *
 * @author Panzer1119 &amp; pcfreak9000
 */
public class OmniKryptecEngine implements Profilable {

	private static OmniKryptecEngine instance;

	static {
		de.codemakers.logger.Logger.setLogger(new ILogger() {
			@Override
			public final void log(Object object, Object... objects) {
				if (objects == null || objects.length == 0) {
					Logger.log(object);
				} else {
					Logger.log(String.format("" + object, objects));
				}
			}

			@Override
			public final void logErr(Object object, Throwable throwable, Object... objects) {
				if (object != null) {
					if (objects == null || objects.length == 0) {
						Logger.logErr(object, (Exception) throwable);
					} else {
						Logger.logErr(String.format("" + object, objects), (Exception) throwable);
					}
				}
			}
		});
	}

	public static OmniKryptecEngine instance() {
		return instance;
	}

	public static void addShutdownHook(Runnable run) {
		if (run == null) {
			return;
		}
		Runtime.getRuntime().addShutdownHook(new Thread(run));
	}

	public static boolean isCreated() {
		return instance != null;
	}

	// public static void main(String[] args) {
	// // TODO say that this is a library?
	// }
	public static enum ShutdownOption {
		JAVA(2), ENGINE(1), NOTHING(0);

		private final int level;

		private ShutdownOption(int level) {
			this.level = level;
		}

		public int getLevel() {
			return level;
		}
	}

	private DisplayManager manager;
	private PostProcessing postpro;
	private final ArrayList<AbstractScene3D> scenes3D = new ArrayList<>();
	private final ArrayList<AbstractScene2D> scenes2D = new ArrayList<>();
	private AbstractScene3D scene3DCurrent;
	private AbstractScene2D scene2DCurrent;
	private GameLoop gameloop;
	private Color clearcolor = new Color(0, 0, 0, 0);
	private ShutdownOption shutdownOption = ShutdownOption.JAVA;
	private GameState state = GameState.STOPPED;

	private boolean cleaned;

	public static final String ENGINE_EVENT_BUS_NAME = "OmnikryptecEvents-3141";

	public final EventBus ENGINE_BUS;

	public OmniKryptecEngine(DisplayManager manager) {
		if (manager == null) {
			throw new NullPointerException("DisplayManager is null");
		}
		if (instance != null) {
			throw new IllegalStateException("OmniKryptec-Engine was already created!");
		}
		ENGINE_BUS = new EventBus(ENGINE_EVENT_BUS_NAME,
				manager.getSettings().getInteger(GameSettings.THREADPOOLSIZE_EVENT_EXECUTION),
				manager.getSettings().getInteger(GameSettings.THREADPOOLSIZE_EVENT_SUBMISSION));
		try {
			cleaned = false;
			addShutdownHook(() -> cleanup(false));
			Profiler.addProfilable(this, 0);
			this.manager = manager;
			state = GameState.STARTING;
			instance = this;
			postpro = new PostProcessing(null);

			RendererRegistration.init();
			this.createFbos();
			Display.show();
			Logger.log("Successfully booted the Engine!", LogLevel.FINEST);
			instance = this;
			if (gameloop == null) {
				gameloop = new DefaultGameLoop();
				Logger.log("Successfully setted a DefaultGameLoop!", LogLevel.FINEST);
			}
		} catch (Exception e) {
			errorOccured(e, "Error occured while booting!");
		}
	}

	public GameState getState() {
		return state;
	}

	public final FrameBufferObject getSceneFBO() {
		return unsampledfbo;
	}

	public final FrameBufferObject getNormalFBO() {
		return normalfbo;
	}

	public final FrameBufferObject getSpecularFBO() {
		return specularfbo;
	}

	public final FrameBufferObject getShaderInfoFBO() {
		return extrainfofbo;
	}

	public final DisplayManager getDisplayManager() {
		return manager;
	}

	public final GameSettings getSettings() {
		return manager == null ? null : manager.getSettings();
	}

	public final PostProcessing getPostprocessor() {
		return postpro;
	}

	public final void startLoop() {
		if (gameloop != null) {
			gameloop.run();
		}
	}

	public final void setLoop(GameLoop loop) {
		boolean b = gameloop != null && gameloop.isRunning();
		if (b) {
			gameloop.requestStop(GameLoopShutdownOption.LOOP);
		}
		gameloop = loop;
		if (b) {
			gameloop.run();
		}
	}

	public final GameLoop getLoop() {
		return gameloop;
	}

	public final boolean hasLoop() {
		return getLoop() != null;
	}

	final void beginScene3dRendering() {
		scenefbo.bindFrameBuffer();
	}

	final void endScene3dRendering() {
		scenefbo.unbindFrameBuffer();
		if (FboModes.enabled(fboMode, FboModes.SCENE)) {
			scenefbo.resolveToFbo(unsampledfbo, GL30.GL_COLOR_ATTACHMENT0);
		}
		if (FboModes.enabled(fboMode, FboModes.NORMALS)) {
			scenefbo.resolveToFbo(normalfbo, GL30.GL_COLOR_ATTACHMENT1);
		}
		if (FboModes.enabled(fboMode, FboModes.SPECULAR)) {
			scenefbo.resolveToFbo(specularfbo, GL30.GL_COLOR_ATTACHMENT2);
		}
		if (FboModes.enabled(fboMode, FboModes.INFO)) {
			scenefbo.resolveToFbo(extrainfofbo, GL30.GL_COLOR_ATTACHMENT3);
		}
		// if (scenefbo.getTargets().length > 4) {
		// for (int i = 4; i < scenefbo.getTargets().length; i++) {
		// scenefbo.resolveToFbo(add[i], manager.getSettings().getAddAttachments()[i -
		// 4].target);
		// }
		// }
	}

	private FrameBufferObject scenefbo;
	private FrameBufferObject unsampledfbo, normalfbo, specularfbo, extrainfofbo;
	// private FrameBufferObject[] add;

	public static class FboModes {
		public static final int SCENE = 0x1;
		public static final int NORMALS = 0x2;
		public static final int SPECULAR = 0x4;
		public static final int INFO = 0x8;

		public static int all() {
			return SCENE | NORMALS | SPECULAR | INFO;
		}

		public static boolean enabled(int modes, int mode) {
			return (modes & mode) == mode;
		}
	}

	private int fboMode = FboModes.all();

	public void setFboModes(int modes) {
		fboMode = modes;
	}

	public void addFboMode(int mode) {
		fboMode |= mode;
	}

	public void removeFboMode(int mode) {
		fboMode &= ~mode;
	}

	private void createFbos() {
		List<RenderTarget> tmp = new ArrayList<>();
		if (FboModes.enabled(fboMode, FboModes.SCENE)) {
			tmp.add(new RenderTarget(GL30.GL_COLOR_ATTACHMENT0,
					manager.getSettings().getInteger(GameSettings.COLORSPACE_SCENE_FBO)));
		}
		if (FboModes.enabled(fboMode, FboModes.NORMALS)) {
			tmp.add(new RenderTarget(GL30.GL_COLOR_ATTACHMENT1,
					manager.getSettings().getInteger(GameSettings.COLORSPACE_NORMAL_FBO)));
		}
		if (FboModes.enabled(fboMode, FboModes.SPECULAR)) {
			tmp.add(new RenderTarget(GL30.GL_COLOR_ATTACHMENT2,
					manager.getSettings().getInteger(GameSettings.COLORSPACE_SPECULAR_FBO)));
		}
		if (FboModes.enabled(fboMode, FboModes.INFO)) {
			tmp.add(new RenderTarget(GL30.GL_COLOR_ATTACHMENT3,
					manager.getSettings().getInteger(GameSettings.COLORSPACE_SHADER_INFO_FBO)));
		}
		scenefbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(),
				manager.getSettings().getMultiSamples(), tmp.toArray(new RenderTarget[tmp.size()]));
		tmp.clear();
		if (FboModes.enabled(fboMode, FboModes.SCENE)) {
			unsampledfbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE,
					new RenderTarget(GL30.GL_COLOR_ATTACHMENT0,
							manager.getSettings().getInteger(GameSettings.COLORSPACE_SCENE_FBO)));
		}
		if (FboModes.enabled(fboMode, FboModes.NORMALS)) {
			normalfbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE,
					new RenderTarget(GL30.GL_COLOR_ATTACHMENT0,
							manager.getSettings().getInteger(GameSettings.COLORSPACE_NORMAL_FBO)));
		}
		if (FboModes.enabled(fboMode, FboModes.SPECULAR)) {
			specularfbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE,
					new RenderTarget(GL30.GL_COLOR_ATTACHMENT0,
							manager.getSettings().getInteger(GameSettings.COLORSPACE_SPECULAR_FBO)));
		}
		if (FboModes.enabled(fboMode, FboModes.INFO)) {
			extrainfofbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE,
					new RenderTarget(GL30.GL_COLOR_ATTACHMENT0,
							manager.getSettings().getInteger(GameSettings.COLORSPACE_SHADER_INFO_FBO)));
		}
	}

	public void refreshFbos() {
		if (scenefbo != null) {
			scenefbo.delete();
		}
		if (unsampledfbo != null) {
			unsampledfbo.delete();
		}
		if (normalfbo != null) {
			normalfbo.delete();
		}
		if (specularfbo != null) {
			specularfbo.delete();
		}
		if (extrainfofbo != null) {
			extrainfofbo.delete();
		}
		createFbos();
	}

	void doPostprocessing() {
		postpro.doPostProcessing(/* add, */ unsampledfbo, normalfbo, specularfbo, extrainfofbo);
	}

	final double getRender3DTimeMS() {
		return scene3DCurrent == null ? 0 : scene3DCurrent.getRenderTimeMS();
	}

	final double getLogic3DTimeMS() {
		return scene3DCurrent == null ? 0 : scene3DCurrent.getLogicTimeMS();
	}

	final double getRender2DTimeMS() {
		return scene2DCurrent == null ? 0 : scene2DCurrent.getRenderTimeMS();
	}

	final double getLogic2DTimeMS() {
		return scene2DCurrent == null ? 0 : scene2DCurrent.getLogicTimeMS();
	}

	/**
	 * Do not call this in a GameLoop-class. Use your own times or the
	 * DisplayManager deltatime instead.
	 *
	 * @return
	 */
	public float getDeltaTimef() {
		return hasLoop() ? gameloop.getDeltaTimef() : manager.getDUDeltaTimef();
	}

	/**
	 * Do not call this in a GameLoop-class. Use your own times or the
	 * DisplayManager deltatime instead.
	 *
	 * @return (smooth)
	 */
	public float getDeltaTimeSf() {
		return hasLoop() ? gameloop.getDeltatimeSmooth() : manager.getDUDeltaTimef();
	}

	public final OmniKryptecEngine close(ShutdownOption shutdownOption) {
		if (gameloop != null) {
			gameloop.requestStop(GameLoopShutdownOption.ENGINE);
		}
		if (shutdownOption.getLevel() >= ShutdownOption.ENGINE.getLevel()) {
			cleanup(true);
			manager.close();
			if (shutdownOption.getLevel() >= ShutdownOption.JAVA.getLevel()) {
				Commands.COMMANDEXIT.run("-java");
			}
			return null;
		} else {
			return this;
		}
	}

	private void cleanup(boolean event) {
		if (cleaned) {
			return;
		}
		RenderChunk3D.cleanup();
		PostProcessing.cleanup();
		VertexArrayObject.cleanup();
		VertexBufferObject.cleanup();
		FrameBufferObject.cleanup();
		SimpleTexture.cleanup();
		Shader.cleanAllShader();
		Query.cleanup();
		if (event) {
			new CleanupEvent().call();
		}
		EventBus.clean();
		instance = null;
		cleaned = true;
	}

	public void errorOccured(Exception e, String text) {
		state = GameState.ERROR;
		new OmnikryptecError(e, new ErrorObject<>(text)).print();
		new ErrorEvent(e, this).call();
		shutdown();
	}

	public final ShutdownOption getShutdownOption() {
		return shutdownOption;
	}

	public final OmniKryptecEngine setShutdownOption(ShutdownOption shutdownOption) {
		this.shutdownOption = shutdownOption;
		return this;
	}

	public void shutdown() {
		close(getShutdownOption());
	}

	public final OmniKryptecEngine addAndSetScene(AbstractScene<?> scene) {
		addScene(scene);
		if (scene instanceof AbstractScene3D) {
			setScene3D(scene.getName());
		} else if (scene instanceof AbstractScene2D) {
			setScene2D(scene.getName());
		}
		return this;
	}

	public final OmniKryptecEngine addScene(AbstractScene<?> scene) {
		if (scene instanceof AbstractScene3D) {
			if (!scenes3D.contains(scene)) {
				scenes3D.add((AbstractScene3D) scene);
			}
		} else if (scene instanceof AbstractScene2D) {
			if (!scenes2D.contains(scene)) {
				scenes2D.add((AbstractScene2D) scene);
			}
		}
		return this;
	}

	public final OmniKryptecEngine setScene3D(String name) {
		List<AbstractScene3D> scenesEquals = scenes3D.stream().filter((scene) -> scene.getName().equals(name))
				.collect(Collectors.toList());
		scene3DCurrent = (scenesEquals.isEmpty() ? null : scenesEquals.get(0));
		return this;
	}

	public final OmniKryptecEngine setScene2D(String name) {
		List<AbstractScene2D> scenesEquals = scenes2D.stream().filter((scene) -> scene.getName().equals(name))
				.collect(Collectors.toList());
		scene2DCurrent = (scenesEquals.isEmpty() ? null : scenesEquals.get(0));
		return this;
	}

	public final AbstractScene3D getCurrent3DScene() {
		return scene3DCurrent;
	}

	public final AbstractScene2D getCurrent2DScene() {
		return scene2DCurrent;
	}

	public final ArrayList<AbstractScene3D> getScenes3D() {
		return scenes3D;
	}

	public final ArrayList<AbstractScene2D> getScenes2D() {
		return scenes2D;
	}

	public String getCurrentScene3DName() {
		return scene3DCurrent == null ? null : scene3DCurrent.getName();
	}

	public String getCurrentScene2DName() {
		return scene2DCurrent == null ? null : scene2DCurrent.getName();
	}

	public boolean hasScene3D() {
		return getCurrent3DScene() != null;
	}

	public boolean hasScene2D() {
		return getCurrent2DScene() != null;
	}

	public final OmniKryptecEngine setClearColor(float r, float g, float b) {
		return setClearColor(r, g, b, 1);
	}

	public final OmniKryptecEngine setClearColor(float r, float g, float b, float a) {
		clearcolor.set(r, g, b, a);
		return this;
	}

	public final OmniKryptecEngine setClearColor(Color f) {
		clearcolor = f;
		return this;
	}

	public final Color getClearColor() {
		return clearcolor;
	}

	public final GameSettings getGameSettings() {
		return getDisplayManager().getSettings();
	}

	@Override
	public ProfileContainer[] getProfiles() {
		return new ProfileContainer[] {
				new ProfileContainer(Profiler.OVERALL_FRAME_TIME, hasLoop() ? getLoop().getFrameTime() : 0),
				new ProfileContainer(Profiler.SCENE_RENDER_TIME_3D, getRender3DTimeMS()),
				new ProfileContainer(Profiler.SCENE_LOGIC_TIME_3D, getLogic3DTimeMS()),
				new ProfileContainer(Profiler.SCENE_RENDER_TIME_2D, getRender2DTimeMS()),
				new ProfileContainer(Profiler.SCENE_LOGIC_TIME_2D, getLogic2DTimeMS()) };
	}
}
