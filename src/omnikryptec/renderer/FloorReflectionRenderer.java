package omnikryptec.renderer;

import java.util.List;

import omnikryptec.gameobject.Entity;
import omnikryptec.main.AbstractScene;
import omnikryptec.postprocessing.main.FrameBufferObject;
import omnikryptec.resource.model.AdvancedModel;
import omnikryptec.shader.base.Shader;
import omnikryptec.util.FrustrumFilter;
import omnikryptec.util.RenderUtil;

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
	}

	@Override
	protected long render(AbstractScene s, RenderMap<AdvancedModel, List<Entity>> entities, Shader started,
			FrustrumFilter filter) {
		texture.bindFrameBuffer();
		RenderUtil.clear(0, 0, 0, 0);
		s.getCamera().reflect(height);
		long l = s.publicRender(config);
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
