package de.omnikryptec.test;

import de.omnikryptec.gameobject.Entity;
import de.omnikryptec.graphics.GraphicsUtil;
import de.omnikryptec.main.AbstractScene3D;
import de.omnikryptec.postprocessing.main.FrameBufferObject;
import de.omnikryptec.renderer.d3.RenderConfiguration;
import de.omnikryptec.renderer.d3.RenderConfiguration.AllowedRenderer;
import de.omnikryptec.renderer.d3.RenderMap;
import de.omnikryptec.renderer.d3.Renderer;
import de.omnikryptec.resource.model.AdvancedModel;
import de.omnikryptec.shader.base.Shader;
import de.omnikryptec.util.EnumCollection.DepthbufferType;
import de.omnikryptec.util.FrustrumFilter;

import java.util.List;

public class TestRenderer extends Renderer{

	private FrameBufferObject fbo = new FrameBufferObject(1280, 720, DepthbufferType.NONE);
	
	public TestRenderer() {
		super(null);
		usesShader = false;
	}

	@Override
	protected long render(AbstractScene3D s, RenderMap<AdvancedModel, List<Entity>> entities, Shader started,
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
