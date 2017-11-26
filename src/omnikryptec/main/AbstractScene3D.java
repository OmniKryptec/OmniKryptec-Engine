package omnikryptec.main;

import java.util.LinkedList;
import java.util.List;

import omnikryptec.event.event.Event;
import omnikryptec.event.event.EventType;
import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.GameObject;
import omnikryptec.gameobject.Light;
import omnikryptec.gameobject.particles.ParticleMaster;
import omnikryptec.physics.JBulletPhysicsWorld;
import omnikryptec.renderer.RenderConfiguration;
import omnikryptec.renderer.Renderer;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.settings.GameSettings;
import omnikryptec.test.saving.DataMapSerializable;
import omnikryptec.util.EnumCollection.FrameState;
import omnikryptec.util.EnumCollection.RendererTime;
import omnikryptec.util.Instance;
import omnikryptec.util.PhysicsUtil;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public abstract class AbstractScene3D extends AbstractScene implements DataMapSerializable {



	public static final AbstractScene3D byName(String name) {
		if (OmniKryptecEngine.instance() != null) {
			for (AbstractScene3D scene : OmniKryptecEngine.instance().getScenes()) {
				if (scene.getName() == null ? name == null : scene.getName().equals(name)) {
					return scene;
				}
			}
			return null;
		} else {
			return null;
		}
	}

	
	private LinkedList<Renderer> prerender = new LinkedList<>();
	private LinkedList<Renderer> postrender = new LinkedList<>();

	protected AbstractScene3D(String name, Camera cam) {
		this.name = name;
		this.camera = cam;
	}

	public final void publicLogic(boolean particles) {
		state = FrameState.LOGIC;
		tmptime = OmniKryptecEngine.instance().getDisplayManager().getCurrentTime();
		if (isUsingPhysics()) {
			physicsworld.stepSimulation();
		}
		logic();
		camera.doLogic();
		logictime = OmniKryptecEngine.instance().getDisplayManager().getCurrentTime() - tmptime;
		if (particles && camera != null) {
			ParticleMaster.instance().logic(camera);
		}
		state = FrameState.NULL;
	}
	
	private boolean dirtyConfig=true;
	private RenderConfiguration defaultConfig;
	final long mainRender() {
		if(dirtyConfig) {
			defaultConfig = getNew();
			dirtyConfig = false;
		}
		ParticleMaster.resetTimes();
		state = FrameState.RENDERING;
		tmptime = OmniKryptecEngine.instance().getDisplayManager().getCurrentTime();
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
		rendertime = OmniKryptecEngine.instance().getDisplayManager().getCurrentTime() - tmptime - ParticleMaster.instance().getRenderTimeMS();
		state = FrameState.NULL;
		return l;
	}

	public final void newRenderConfig() {
		dirtyConfig = true;
	}
	
	public final RenderConfiguration getRenderConfig() {
		if(dirtyConfig) {
			defaultConfig = getNew();
			dirtyConfig = false;
			return defaultConfig;
		}
		return defaultConfig;
	}
	
	private final RenderConfiguration getNew() {
		RenderConfiguration cfg = new RenderConfiguration().setShaderLvl(Instance.getGameSettings().getInteger(GameSettings.HIGHEST_SHADER_LVL));
		OmniKryptecEngine.instance().getEventsystem().fireEvent(new Event(cfg), EventType.NEW_DEFAULT_RENDERCONFIGURATION);
		return cfg;
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

	public final AbstractScene3D addIndependentRenderer(Renderer r, RendererTime t) {
		if(r == null) {
			if(Logger.isDebugMode()) {
				Logger.log("Renderer is null!", LogLevel.WARNING);
			}
			return this;
		}
		RendererRegistration.exceptionIfNotRegistered(r);
		if (t == RendererTime.PRE) {
			prerender.add(r);
		}
		if(t == RendererTime.POST) {
			postrender.add(r);
		}
		return this;
	}

	public final AbstractScene3D removeIndependentRenderer(Renderer r, RendererTime t) {
		if (r != null && t == RendererTime.PRE) {
			prerender.remove(r);
		}
		if(r != null && t == RendererTime.POST) {
			postrender.remove(r);
		}
		return this;
	}

	public final AbstractScene3D useDefaultPhysics() {
		return (AbstractScene3D)setPhysicsWorld(new JBulletPhysicsWorld(PhysicsUtil.createDefaultDynamicsWorld()));
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
