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

import java.util.List;

import de.omnikryptec.old.gameobject.Entity;
import de.omnikryptec.old.graphics.GraphicsUtil;
import de.omnikryptec.old.main.AbstractScene3D;
import de.omnikryptec.old.postprocessing.main.FrameBufferObject;
import de.omnikryptec.old.renderer.d3.RenderConfiguration;
import de.omnikryptec.old.renderer.d3.RenderConfiguration.AllowedRenderer;
import de.omnikryptec.old.renderer.d3.Renderer;
import de.omnikryptec.old.resource.model.AdvancedModel;
import de.omnikryptec.old.shader.base.Shader;
import de.omnikryptec.old.util.EnumCollection.DepthbufferType;
import de.omnikryptec.old.util.FrustrumFilter;
import de.omnikryptec.old.util.KeyArrayHashMap;

public class TestRenderer extends Renderer {

    private FrameBufferObject fbo = new FrameBufferObject(1280, 720, DepthbufferType.NONE);

    public TestRenderer() {
	super(null);
	usesShader = false;
    }

    @Override
    protected long render(AbstractScene3D s, KeyArrayHashMap<AdvancedModel, List<Entity>> entities, Shader started,
	    FrustrumFilter filter) {
	fbo.bindFrameBuffer();
	GraphicsUtil.clear(0, 0, 0, 0);
	s.setTmpRenderConfig(new RenderConfiguration().setRendererData(AllowedRenderer.EvElse, this));
	s.publicRender();
	s.setUnTmpRenderConfig();
	fbo.unbindFrameBuffer();
	return 0;
    }

    public FrameBufferObject getFBO() {
	return fbo;
    }

}
