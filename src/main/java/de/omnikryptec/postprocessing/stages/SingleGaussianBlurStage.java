package de.omnikryptec.postprocessing.stages;

import omnikryptec.display.Display;
import omnikryptec.postprocessing.main.FrameBufferObject;
import omnikryptec.postprocessing.main.PostProcessingStageShaded;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.files.postprocessing.GaussianBlurShader;
import omnikryptec.util.EnumCollection.DepthbufferType;

import java.util.List;

public class SingleGaussianBlurStage extends PostProcessingStageShaded {

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
		(l_ind < 0 ? before : beforelist.get(l_ind)).bindToUnitOptimized(0);
	}

	@Override
	public FrameBufferObject createFbo() {
		return new FrameBufferObject((int) (Display.getWidth() * (scalefbo ? w : 1)),
				(int) (Display.getHeight() * (scalefbo ? h : 1)), DepthbufferType.DEPTH_TEXTURE);
	}

}
