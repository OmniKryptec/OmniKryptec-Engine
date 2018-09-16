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

package de.omnikryptec.renderer.d3;

import java.util.List;

import org.joml.Vector4f;

import de.omnikryptec.gameobject.Entity;
import de.omnikryptec.graphics.GraphicsUtil;
import de.omnikryptec.main.AbstractScene3D;
import de.omnikryptec.postprocessing.main.FrameBufferObject;
import de.omnikryptec.resource.model.AdvancedModel;
import de.omnikryptec.shader.base.Shader;
import de.omnikryptec.util.EnumCollection.RendererTime;
import de.omnikryptec.util.FrustrumFilter;
import de.omnikryptec.util.Instance;
import de.omnikryptec.util.KeyArrayHashMap;


public class FloorReflectionRenderer extends Renderer{
	
	private RenderConfiguration config;
	private FrameBufferObject texture;
	private float height=0;
	
	public FloorReflectionRenderer(RenderConfiguration c, FrameBufferObject texture, float height) {
		this.config = c;
		this.texture = texture;
		if(this.texture == null) {
			throw new NullPointerException("FBO is null!");
		}
		config.setClipPlane(new Vector4f(0, 1, 0, height));
	}

	public FloorReflectionRenderer registerAndAddToCurrentScene() {
		RendererRegistration.register(this);
		Instance.getCurrent3DScene().addIndependentRenderer(this, RendererTime.PRE);
		return this;
	}
	
	@Override
	protected long render(AbstractScene3D s, KeyArrayHashMap<AdvancedModel, List<Entity>> entities, Shader started,
			FrustrumFilter filter) {
		texture.bindFrameBuffer();
		GraphicsUtil.clear(0, 0, 0, 1);
		s.getCamera().reflect(height);
		s.setTmpRenderConfig(config.clone().setShaderLvl(0));
		long l = s.publicRender();
		s.getCamera().reflect(height);
		texture.unbindFrameBuffer();
		return l;
	}
	
	public FrameBufferObject getTexture() {
		return texture;
	}
	
	public RenderConfiguration getRenderConfig() {
		return config;
	}
}
