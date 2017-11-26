package omnikryptec.main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL30;

import omnikryptec.display.Display;
import omnikryptec.display.DisplayManager;
import omnikryptec.event.event.Event;
import omnikryptec.event.event.EventSystem;
import omnikryptec.event.event.EventType;
import omnikryptec.postprocessing.main.FrameBufferObject;
import omnikryptec.postprocessing.main.FrameBufferObject.DepthbufferType;
import omnikryptec.postprocessing.main.PostProcessing;
import omnikryptec.postprocessing.main.RenderTarget;
import omnikryptec.renderer.Query;
import omnikryptec.renderer.RenderChunk;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.resource.model.VertexArrayObject;
import omnikryptec.resource.model.VertexBufferObject;
import omnikryptec.resource.texture.SimpleTexture;
import omnikryptec.settings.GameSettings;
import omnikryptec.shader.base.Shader;
import omnikryptec.util.Color;
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

	public static OmniKryptecEngine instance() {
		return instance;
	}

	public static void addShutdownHook(Runnable run) {
		if (run == null) {
			return;
		}
		Runtime.getRuntime().addShutdownHook(new Thread(run));
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		// TODO say that this is a library?
	}

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

	private GameState state = GameState.STOPPED;

	public GameState getState() {
		return state;
	}

	private DisplayManager manager;
	private EventSystem eventsystem;
	private PostProcessing postpro;
	private final ArrayList<Abstract3DEnv> scenes = new ArrayList<>();
	private Abstract3DEnv sceneCurrent;
	private Color clearcolor = new Color(0, 0, 0, 0);
	private long vertsCountCurrent = 0;

	private ShutdownOption shutdownOption = ShutdownOption.JAVA;
	private double frametime = 0;
	private GameLoop gameloop;
	
	public OmniKryptecEngine(DisplayManager manager) {
		if (manager == null) {
			throw new NullPointerException("DisplayManager is null");
		}
		if (instance != null) {
			throw new IllegalStateException("OmniKryptec-Engine was already created!");
		}
		try {
			Profiler.addProfilable(this, 0);
			this.manager = manager;
			state = GameState.STARTING;
			instance = this;
			eventsystem = new EventSystem();
			postpro = new PostProcessing(null);
			
			RendererRegistration.init();
			this.createFbos();
			Display.show();
			eventsystem.fireEvent(new Event(), EventType.BOOTING_COMPLETED);
			Logger.log("Successfully booted the Engine!", LogLevel.FINEST);
			instance = this;
			if(gameloop==null) {
				gameloop = new DefaultGameLoop();
			}
		} catch (Exception e) {
			errorOccured(e, "Error occured while booting!");
		}
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

	public final EventSystem getEventsystem() {
		return eventsystem;
	}

	public final PostProcessing getPostprocessor() {
		return postpro;
	}


	public final void startLoop() {
		if(gameloop!=null) {
			gameloop.run();
		}
	}

	public final void setLoop(GameLoop loop) {
		boolean b = gameloop!=null&&gameloop.isRunning();
		if(b) {
			gameloop.requestStop(GameLoopShutdownOption.LOOP);
		}
		gameloop = loop;
		if(b) {
			gameloop.run();
		}
	}
	
	public final GameLoop getLoop() {
		return gameloop;
	}
	
	public final boolean hasLoop() {
		return getLoop()!=null;
	}
	
	final void beginSceneRendering() {
		scenefbo.bindFrameBuffer();
	}
	
	final void endSceneRendering() {
		scenefbo.unbindFrameBuffer();
		scenefbo.resolveToFbo(unsampledfbo, GL30.GL_COLOR_ATTACHMENT0);
		scenefbo.resolveToFbo(normalfbo, GL30.GL_COLOR_ATTACHMENT1);
		scenefbo.resolveToFbo(specularfbo, GL30.GL_COLOR_ATTACHMENT2);
		scenefbo.resolveToFbo(extrainfofbo, GL30.GL_COLOR_ATTACHMENT3);
		if (scenefbo.getTargets().length > 4) {
			for (int i = 4; i < scenefbo.getTargets().length; i++) {
				scenefbo.resolveToFbo(add[i], manager.getSettings().getAddAttachments()[i - 4].target);
			}
		}
	}
	
	private FrameBufferObject scenefbo;
	private FrameBufferObject unsampledfbo, normalfbo, specularfbo, extrainfofbo;
	private FrameBufferObject[] add;

	private void createFbos() {
		scenefbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(),
				manager.getSettings().getMultiSamples(), manager.getSettings().getAddAttachments(),
				new RenderTarget(GL30.GL_COLOR_ATTACHMENT0,
						manager.getSettings().getInteger(GameSettings.COLORSPACE_SCENE_FBO)),
				new RenderTarget(GL30.GL_COLOR_ATTACHMENT1,
						manager.getSettings().getInteger(GameSettings.COLORSPACE_NORMAL_FBO)),
				new RenderTarget(GL30.GL_COLOR_ATTACHMENT2,
						manager.getSettings().getInteger(GameSettings.COLORSPACE_SPECULAR_FBO)),
				new RenderTarget(GL30.GL_COLOR_ATTACHMENT3,
						manager.getSettings().getInteger(GameSettings.COLORSPACE_SHADER_INFO_FBO)));
		unsampledfbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE,
				new RenderTarget(GL30.GL_COLOR_ATTACHMENT0, manager.getSettings().getInteger(GameSettings.COLORSPACE_SCENE_FBO)));
		normalfbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE,
				new RenderTarget(GL30.GL_COLOR_ATTACHMENT0, manager.getSettings().getInteger(GameSettings.COLORSPACE_NORMAL_FBO)));
		specularfbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE,
				new RenderTarget(GL30.GL_COLOR_ATTACHMENT0,manager.getSettings().getInteger(GameSettings.COLORSPACE_SPECULAR_FBO)));
		extrainfofbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE,
				new RenderTarget(GL30.GL_COLOR_ATTACHMENT0, manager.getSettings().getInteger(GameSettings.COLORSPACE_SHADER_INFO_FBO)));
		add = manager.getSettings().getAddFBOs();
	}

	void resizeFbos() {
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
		postpro.doPostProcessing(add, unsampledfbo, normalfbo, specularfbo, extrainfofbo);
	}
	
	public final long getModelVertsCount() {
		return vertsCountCurrent;
	}

	final double getRenderTimeMS() {
		return sceneCurrent == null ? 0 : sceneCurrent.getRenderTimeMS();
	}

	final double getLogicTimeMS() {
		return sceneCurrent == null ? 0 : sceneCurrent.getLogicTimeMS();
	}

	final double getFrameTimeMS() {
		return frametime;
	}

	/**
	 * Do not call this in a GameLoop-class. Use your own times or the DisplayManager deltatime instead.
	 * @return
	 */
	public float getDeltaTimef() {
		return hasLoop()?gameloop.getDeltaTimef():manager.getDUDeltaTimef();
	}
	
	public final OmniKryptecEngine close(ShutdownOption shutdownOption) {
		
		if(gameloop!=null) {
			gameloop.requestStop(GameLoopShutdownOption.ENGINE);
		}
		if (shutdownOption.getLevel() >= ShutdownOption.ENGINE.getLevel()) {
			cleanup();
			manager.close();
			if (shutdownOption.getLevel() >= ShutdownOption.JAVA.getLevel()) {
				Commands.COMMANDEXIT.run("-java");
			}
			return null;
		} else {
			return this;
		}
	}

	private void cleanup() {
		RenderChunk.cleanup();
		PostProcessing.cleanup();
		VertexArrayObject.cleanup();
		VertexBufferObject.cleanup();
		FrameBufferObject.cleanup();
		SimpleTexture.cleanup();
		Shader.cleanAllShader();
		Query.cleanup();
		eventsystem.fireEvent(new Event(), EventType.CLEANUP);
		EventSystem.cleanUp();
		instance = null;
	}

	public void errorOccured(Exception e, String text) {
		state = GameState.ERROR;
		new OmnikryptecError(e, new ErrorObject<String>(text)).print();
		eventsystem.fireEvent(new Event(e), EventType.ERROR);
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
	
	public final OmniKryptecEngine addAndSetScene(Environment scene) {
		if (scene != null) {
			addScene(scene);
			if(scene instanceof Abstract3DEnv) {
				setScene(scene.getName());
			}else if(scene instanceof Abstract2DEnv) {
				
			}
		}
		return this;
	}

	public final OmniKryptecEngine addScene(Environment scene) {
		if (scene != null) {
			if(scene instanceof Abstract3DEnv) {
				scenes.add((Abstract3DEnv)scene);
			}else if(scene instanceof Abstract2DEnv) {
				
			}
		}
		return this;
	}

	public final OmniKryptecEngine setScene(String name) {
		List<Abstract3DEnv> scenesEquals = scenes.stream().filter((scene) -> scene.getName().equals(name))
				.collect(Collectors.toList());
		sceneCurrent = (scenesEquals.isEmpty() ? null : scenesEquals.get(0));
		return this;
	}

	public final Abstract3DEnv getCurrent3DScene() {
		return sceneCurrent;
	}



	
	public final ArrayList<Abstract3DEnv> getScenes() {
		return scenes;
	}

	public String getCurrentSceneName() {
		return sceneCurrent == null ? null : sceneCurrent.getName();
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
	
	@Override
	public ProfileContainer[] getProfiles() {
		return new ProfileContainer[] { new ProfileContainer(Profiler.OVERALL_FRAME_TIME, getFrameTimeMS()),
				new ProfileContainer(Profiler.SCENE_RENDER_TIME, getRenderTimeMS()),
				new ProfileContainer(Profiler.SCENE_LOGIC_TIME, getLogicTimeMS()) };
	}

	public static boolean isCreated() {
		return instance != null;
	}

	public boolean hasEnvironment() {
		return getCurrent3DScene()!=null;
	}
}
