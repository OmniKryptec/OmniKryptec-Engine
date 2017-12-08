package omnikryptec.main;

import java.util.LinkedList;
import java.util.List;

import omnikryptec.event.event.Event;
import omnikryptec.event.event.EventType;
import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.GameObject3D;
import omnikryptec.gameobject.Light3D;
import omnikryptec.gameobject.particles.ParticleMaster;
import omnikryptec.physics.JBulletPhysicsWorld;
import omnikryptec.renderer.d3.RenderConfiguration;
import omnikryptec.renderer.d3.Renderer;
import omnikryptec.renderer.d3.RendererRegistration;
import omnikryptec.settings.GameSettings;
import omnikryptec.test.saving.DataMapSerializable;
import omnikryptec.util.EnumCollection.FrameState;
import omnikryptec.util.EnumCollection.RendererTime;
import omnikryptec.util.Instance;
import omnikryptec.util.PhysicsUtil;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public abstract class AbstractScene3D extends AbstractScene<GameObject3D> implements DataMapSerializable {



	public static final AbstractScene3D byName(String name) {
		if (OmniKryptecEngine.instance() != null) {
			for (AbstractScene3D scene : OmniKryptecEngine.instance().getScenes3D()) {
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
	private RenderConfiguration renderConfig = new RenderConfiguration();
	private RenderConfiguration backup;
	
	protected AbstractScene3D(String name, Camera cam) {
		this.name = name;
		this.camera = cam;
	}
	

	
	protected void preRender() {
		ParticleMaster.resetTimes();
		if(renderConfig.isRendererTimeAllowed(RendererTime.PRE)) {
			for (Renderer r : prerender) {
				if (renderConfig.getRenderer().contains(r)) {
					r.render(this, null, renderConfig);
					setUnTmpRenderConfig();
				}
			}
		}
	}
	
	protected void postRender() {
		if(renderConfig.isRendererTimeAllowed(RendererTime.POST)) {
			for (Renderer r : postrender) {
				if (renderConfig.getRenderer().contains(r)) {
					r.render(this, null, renderConfig);
					setUnTmpRenderConfig();
				}
			}
		}
	}
	
	public final RenderConfiguration getRenderConfig() {
		return renderConfig;
	}
	
	public final AbstractScene3D setRenderConfig(RenderConfiguration config) {
		this.renderConfig = config;
		return this;
	}
	
	public final AbstractScene3D setTmpRenderConfig(RenderConfiguration config) {
		if(config != null) {
			this.backup = this.renderConfig;
			this.renderConfig = config;
		}
		return this;
	}
	
	//Direkt nach postrender machen?
	public final AbstractScene3D setUnTmpRenderConfig() {
		if(backup!=null) {
			this.renderConfig = backup;
			this.backup = null;
		}
		return this;
	}
	
	public final void publicParticlesRender() {
		if(camera != null) {
			ParticleMaster.instance().render(camera);
		}
	}
	
	public final void publicParticlesLogic() {
		if (camera != null) {
			ParticleMaster.instance().logic(camera);
		}
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

	public abstract List<Light3D> getLights();
}
