package omnikryptec.test;

import java.util.List;

import omnikryptec.entity.Entity;
import omnikryptec.main.Scene;
import omnikryptec.model.TexturedModel;
import omnikryptec.postprocessing.FrameBufferObject;
import omnikryptec.renderer.IRenderer;
import omnikryptec.renderer.RenderChunk.AllowedRenderer;
import omnikryptec.renderer.RenderMap;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.util.RenderUtil;

public class TestFBORenderer implements IRenderer {
	
	public FrameBufferObject fbo;
	
	@Override
	public void render(Scene s, RenderMap<TexturedModel, List<Entity>> entities) {
		fbo.bindFrameBuffer();
		RenderUtil.clear(0, 0, 0, 0);
		s.frame(0, 0, AllowedRenderer.EvElse, this);
		fbo.unbindFrameBuffer();
		RendererRegistration.DEF_ENTITY_RENDERER.render(s, entities);
	}

	@Override
	public void cleanup() {
	}

	@Override
	public float expensiveLevel() {
		return 1;
	}

}
