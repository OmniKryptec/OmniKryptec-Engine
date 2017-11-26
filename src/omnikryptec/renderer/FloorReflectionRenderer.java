package omnikryptec.renderer;

import java.util.List;

import org.joml.Vector4f;

import omnikryptec.gameobject.Entity;
import omnikryptec.main.AbstractScene3D;
import omnikryptec.postprocessing.main.FrameBufferObject;
import omnikryptec.resource.model.AdvancedModel;
import omnikryptec.shader.base.Shader;
import omnikryptec.util.EnumCollection.RendererTime;
import omnikryptec.util.FrustrumFilter;
import omnikryptec.util.GraphicsUtil;
import omnikryptec.util.Instance;

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
		Instance.getCurrentScene().addIndependentRenderer(this, RendererTime.PRE);
		return this;
	}
	
	@Override
	protected long render(AbstractScene3D s, RenderMap<AdvancedModel, List<Entity>> entities, Shader started,
			FrustrumFilter filter) {
		texture.bindFrameBuffer();
		GraphicsUtil.clear(0, 0, 0, 1);
		s.getCamera().reflect(height);
		long l = s.publicRender(config.setShaderLvl(0));
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
