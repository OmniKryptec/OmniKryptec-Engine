package omnikryptec.main;

import java.util.LinkedList;
import java.util.List;

import omnikryptec.display.DisplayManager;
import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.GameObject;
import omnikryptec.gameobject.Light;
import omnikryptec.gameobject.particles.ParticleMaster;
import omnikryptec.physics.JBulletPhysicsWorld;
import omnikryptec.physics.PhysicsWorld;
import omnikryptec.renderer.RenderConfiguration;
import omnikryptec.renderer.Renderer;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.settings.GameSettings;
import omnikryptec.test.saving.DataMapSerializable;
import omnikryptec.util.Color;
import omnikryptec.util.Instance;
import omnikryptec.util.PhysicsUtil;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public abstract class AbstractScene implements DataMapSerializable {

	public static enum FrameState {
		NULL, RENDERING, LOGIC;
	}

	public static enum RendererTime {
		PRE, POST;
	}

	public static final AbstractScene byName(String name) {
		if (OmniKryptecEngine.rawInstance() != null) {
			for (AbstractScene scene : OmniKryptecEngine.rawInstance().getScenes()) {
				if (scene.getName() == null ? name == null : scene.getName().equals(name)) {
					return scene;
				}
			}
			return null;
		} else {
			return null;
		}
	}

	private Camera camera;
	private FrameState state = FrameState.NULL;
	private double rendertime, logictime;
	private double tmptime;
	private String name;
	private PhysicsWorld physicsworld;
	private Color clearcolor = new Color(0, 0, 0, 0);
	private Color ambientcolor = new Color(0.01f, 0.01f, 0.01f, 1);
	private LinkedList<Renderer> prerender = new LinkedList<>();
	private LinkedList<Renderer> postrender = new LinkedList<>();

	protected AbstractScene(String name, Camera cam) {
		this.name = name;
		this.camera = cam;
	}

	public final void publicLogic(boolean particles) {
		state = FrameState.LOGIC;
		tmptime = DisplayManager.instance().getCurrentTime();
		if (isUsingPhysics()) {
			physicsworld.stepSimulation();
		}
		logic();
		camera.doLogic();
		logictime = DisplayManager.instance().getCurrentTime() - tmptime;
		if (particles && camera != null) {
			ParticleMaster.instance().logic(camera);
		}
		state = FrameState.NULL;
	}
	
	private boolean dirtyConfig=true;
	private RenderConfiguration defaultConfig;
	final long mainRender() {
		if(dirtyConfig) {
			defaultConfig = new RenderConfiguration();
			dirtyConfig = false;
		}
		ParticleMaster.resetTimes();
		state = FrameState.RENDERING;
		tmptime = DisplayManager.instance().getCurrentTime();
		if(defaultConfig.isRendererTimeAllowed(RendererTime.PRE)) {
			for (Renderer r : prerender) {
				if (defaultConfig.getRenderer().contains(r)) {
					r.render(this, null, defaultConfig);
				}
			}
		}
		long l = publicRender(defaultConfig);
		if(defaultConfig.isRendererTimeAllowed(RendererTime.POST)) {
			for (Renderer r : postrender) {
				if (defaultConfig.getRenderer().contains(r)) {
					r.render(this, null, defaultConfig);
				}
			}
		}
		rendertime = DisplayManager.instance().getCurrentTime() - tmptime - ParticleMaster.instance().getRenderTimeMS();
		state = FrameState.NULL;
		return l;
	}

	public final void newRenderConfig() {
		dirtyConfig = true;
	}
	
	public final RenderConfiguration getRenderConfig() {
		if(dirtyConfig) {
			defaultConfig = new RenderConfiguration();
			dirtyConfig = false;
			return defaultConfig;
		}
		return defaultConfig;
	}
	
	public final long publicRender(RenderConfiguration config) {
		if(config==null) {
			config = new RenderConfiguration();
		}
		long l = render(config);
		if (config.renderParticles() && camera != null) {
			ParticleMaster.instance().render(camera);
		}
		return l;
	}

	public final AbstractScene addIndependentRenderer(Renderer r, RendererTime t) {
		if(r == null) {
			if(Logger.isDebugMode()) {
				Logger.log("Renderer is null!", LogLevel.WARNING);
			}
			return this;
		}
		RendererRegistration.exceptionsIfNotRegistered(r);
		if (t == RendererTime.PRE) {
			prerender.add(r);
		}
		if(t == RendererTime.POST) {
			postrender.add(r);
		}
		return this;
	}

	public final AbstractScene removeIndependentRenderer(Renderer r, RendererTime t) {
		if (r != null && t == RendererTime.PRE) {
			prerender.remove(r);
		}
		if(r != null && t == RendererTime.POST) {
			postrender.remove(r);
		}
		return this;
	}
	
	public final FrameState getState() {
		return state;
	}

	public final Camera getCamera() {
		return camera;
	}

	public final AbstractScene setCamera(Camera c) {
		this.camera = c;
		return this;
	}

	public final double getRenderTimeMS() {
		return rendertime;
	}

	public final double getLogicTimeMS() {
		return logictime;
	}

	@Override
	public final String getName() {
		return name;
	}

	protected final AbstractScene setName(String name) {
		this.name = name;
		return this;
	}

	public final PhysicsWorld getPhysicsWorld() {
		return physicsworld;
	}

	public final AbstractScene setPhysicsWorld(PhysicsWorld physicsWorld) {
		this.physicsworld = physicsWorld;
		return this;
	}

	public final AbstractScene useDefaultPhysics() {
		return setPhysicsWorld(new JBulletPhysicsWorld(PhysicsUtil.createDefaultDynamicsWorld()));
	}

	public final boolean isUsingPhysics() {
		return physicsworld != null;
	}

	public final AbstractScene setClearColor(float r, float g, float b) {
		return setClearColor(r, g, b, 1);
	}

	public final AbstractScene setClearColor(float r, float g, float b, float a) {
		clearcolor.set(r, g, b, a);
		return this;
	}

	public final AbstractScene setClearColor(Color f) {
		clearcolor = f;
		return this;
	}

	public final Color getClearColor() {
		return clearcolor;
	}

	public final AbstractScene setAmbientColor(float r, float g, float b) {
		this.ambientcolor.set(r, g, b);
		return this;
	}

	public final AbstractScene setAmbientColor(Color f) {
		this.ambientcolor = f;
		return this;
	}

	public final Color getAmbientColor() {
		return ambientcolor;
	}

	protected abstract void logic();

	protected abstract long render(RenderConfiguration config);

	public abstract boolean addGameObject(GameObject go);

	public GameObject removeGameObject(GameObject go) {
		return removeGameObject(go, true);
	}

	public abstract GameObject removeGameObject(GameObject go, boolean delete);

	public abstract List<Light> getLights();
}
