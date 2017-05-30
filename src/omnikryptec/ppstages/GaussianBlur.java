package omnikryptec.ppstages;

import java.util.List;

import org.lwjgl.opengl.Display;

import omnikryptec.postprocessing.FrameBufferObject;
import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.postprocessing.PostProcessingStep;
import omnikryptec.shader.Shader;
import omnikryptec.shader_files.GaussianBlurShader;

public class GaussianBlur extends PostProcessingStep{

	private static GaussianBlurShader shader = new GaussianBlurShader("gaussian_blur_vert.glsl");
	private boolean isHorizontal;
	private float w,h;
	
	public GaussianBlur(boolean ishor, float widthmult, float heightmult) {
		super(shader);
		this.isHorizontal = ishor;
		w = widthmult;
		h = heightmult;
	}

	@Override
	public void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using, int stage) {
		GaussianBlurShader.isHor.loadBoolean(isHorizontal);
		GaussianBlurShader.size.loadFloat(isHorizontal?Display.getWidth()*w:Display.getHeight()*h);
		before.bindToUnit(0);
	}

	@Override
	public void afterRendering() {
		
	}

	@Override
	public FrameBufferObject getOnResize() {
		return new FrameBufferObject((int)(Display.getWidth()*w), (int)(Display.getHeight()*h), DepthbufferType.DEPTH_TEXTURE);
	}

}
