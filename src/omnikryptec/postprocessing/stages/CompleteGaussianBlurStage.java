package omnikryptec.postprocessing.stages;

import java.util.List;

import omnikryptec.postprocessing.main.FrameBufferObject;
import omnikryptec.postprocessing.main.PostProcessingStage;

public class CompleteGaussianBlurStage extends PostProcessingStage {

	private SingleGaussianBlurStage hb, vb;

	public CompleteGaussianBlurStage(boolean scalefbo, float wm, float hm) {
		hb = new SingleGaussianBlurStage(true, wm, hm, scalefbo);
		vb = new SingleGaussianBlurStage(false, wm, hm, scalefbo);
	}

	public void setListIndex(int i) {
		hb.setListIndex(i);
	}

	@Override
	public void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage) {
		hb.renderAndResolveDepth(before, beforelist, stage);
		vb.renderAndResolveDepth(hb.getFbo(), beforelist, stage);
	}

	@Override
	public FrameBufferObject getFbo() {
		return vb.getFbo();
	}

	@Override
	public void onResize() {
		hb.resize();
		vb.resize();
	}

	@Override
	protected FrameBufferObject createFbo() {
		return null;
	}

}
