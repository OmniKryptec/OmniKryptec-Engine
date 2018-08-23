package de.omnikryptec.renderer.d3;

import de.omnikryptec.gameobject.Entity;
import de.omnikryptec.postprocessing.main.FrameBufferObject;
import de.omnikryptec.resource.model.AdvancedModel;
import omnikryptec.graphics.GraphicsUtil;
import omnikryptec.main.AbstractScene3D;
import omnikryptec.shader.base.Shader;
import omnikryptec.util.EnumCollection.RendererTime;
import omnikryptec.util.FrustrumFilter;
import omnikryptec.util.Instance;
import org.joml.Vector4f;

import java.util.List;


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
	protected long render(AbstractScene3D s, RenderMap<AdvancedModel, List<Entity>> entities, Shader started,
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
