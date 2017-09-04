package omnikryptec.test;

import java.util.List;

import omnikryptec.gameobject.Entity;
import omnikryptec.main.AbstractScene;
import omnikryptec.postprocessing.main.FrameBufferObject;
import omnikryptec.postprocessing.main.FrameBufferObject.DepthbufferType;
import omnikryptec.renderer.RenderConfiguration;
import omnikryptec.renderer.RenderConfiguration.AllowedRenderer;
import omnikryptec.renderer.RenderMap;
import omnikryptec.renderer.Renderer;
import omnikryptec.resource.model.AdvancedModel;
import omnikryptec.shader.base.Shader;
import omnikryptec.util.FrustrumFilter;
import omnikryptec.util.RenderUtil;

public class TestRenderer extends Renderer{

	private FrameBufferObject fbo = new FrameBufferObject(1280, 720, DepthbufferType.NONE);
	
	public TestRenderer() {
		super(null);
		usesShader = false;
	}

	@Override
	protected long render(AbstractScene s, RenderMap<AdvancedModel, List<Entity>> entities, Shader started,
			FrustrumFilter filter) {
		fbo.bindFrameBuffer();
		RenderUtil.clear(0, 0, 0, 0);
		s.publicRender(new RenderConfiguration().setRendererData(AllowedRenderer.EvElse, this));
		fbo.unbindFrameBuffer();
		return 0;
	}
	
	public FrameBufferObject getFBO() {
		return fbo;
	}
	
}
