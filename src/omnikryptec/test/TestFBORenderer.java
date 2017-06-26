package omnikryptec.test;

import java.util.List;

import omnikryptec.entity.Entity;
import omnikryptec.main.Scene;
import omnikryptec.model.AdvancedModel;
import omnikryptec.postprocessing.FrameBufferObject;
import omnikryptec.renderer.RenderChunk.AllowedRenderer;
import omnikryptec.renderer.RenderMap;
import omnikryptec.renderer.Renderer;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.util.RenderUtil;

public class TestFBORenderer implements Renderer {

	public FrameBufferObject fbo;

	@Override
	public long render(Scene s, RenderMap<AdvancedModel, List<Entity>> entities, boolean b) {
		fbo.bindFrameBuffer();
		RenderUtil.clear(0, 0, 0, 0);
		s.frame(1000, -1000, b, AllowedRenderer.EvElse, this);
		fbo.unbindFrameBuffer();
		return RendererRegistration.DEF_ENTITY_RENDERER.render(s, entities, b);
	}

	@Override
	public void cleanup() {
	}

	@Override
	public float expensiveLevel() {
		return 1;
	}

}
