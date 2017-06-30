package omnikryptec.postprocessing.stages;

import java.util.List;

import omnikryptec.display.Display;
import omnikryptec.postprocessing.main.FrameBufferObject;
import omnikryptec.postprocessing.main.PostProcessingStep;
import omnikryptec.postprocessing.main.FrameBufferObject.DepthbufferType;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.files.GaussianBlurShader;

public class SingleGaussianBlurStage extends PostProcessingStep {

	private static GaussianBlurShader shader = new GaussianBlurShader("gaussian_blur_vert.glsl");
	private boolean isHorizontal, scalefbo;
	private float w, h;

	public SingleGaussianBlurStage(boolean ishor, float widthmult, float heightmult, boolean scaleFbo) {
		super(shader);
		this.isHorizontal = ishor;
		w = widthmult;
		h = heightmult;
		scalefbo = scaleFbo;
	}

	private int l_ind = -1;

	public SingleGaussianBlurStage setListIndex(int i) {
		l_ind = i;
		return this;
	}

	@Override
	public void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using, int stage) {
		GaussianBlurShader.isHor.loadBoolean(isHorizontal);
		GaussianBlurShader.size.loadFloat(isHorizontal ? Display.getWidth() * w : Display.getHeight() * h);
		(l_ind < 0 ? before : beforelist.get(l_ind)).bindToUnit(0);
	}

	@Override
	public FrameBufferObject createFbo() {
		return new FrameBufferObject((int) (Display.getWidth() * (scalefbo ? w : 1)),
				(int) (Display.getHeight() * (scalefbo ? h : 1)), DepthbufferType.DEPTH_TEXTURE);
	}

}