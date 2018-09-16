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

package de.omnikryptec.main;

import java.util.LinkedList;
import java.util.List;

import de.omnikryptec.gameobject.Camera;
import de.omnikryptec.gameobject.GameObject3D;
import de.omnikryptec.gameobject.Light3D;
import de.omnikryptec.gameobject.particles.ParticleMaster;
import de.omnikryptec.physics.JBulletPhysicsWorld;
import de.omnikryptec.renderer.d3.RenderConfiguration;
import de.omnikryptec.renderer.d3.Renderer;
import de.omnikryptec.renderer.d3.RendererRegistration;
import de.omnikryptec.test.saving.DataMapSerializable;
import de.omnikryptec.util.EnumCollection.RendererTime;
import de.omnikryptec.util.PhysicsUtil;
import de.omnikryptec.util.logger.LogLevel;
import de.omnikryptec.util.logger.Logger;

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
	
	@Override
	public final void addGameObject(GameObject3D go) {
		super.addGameObject(go);
		if(go.hasChilds()){
			for(GameObject3D g : go.getChilds()){
				addGameObject(g);
			}
		}
	}
	
	@Override
	public final GameObject3D removeGameObject(GameObject3D go, boolean delete) {
		super.removeGameObject(go, delete);
		if(go.hasChilds()){
			for(GameObject3D g : go.getChilds()){
				removeGameObject(g, delete);
			}
		}
		return go;
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
