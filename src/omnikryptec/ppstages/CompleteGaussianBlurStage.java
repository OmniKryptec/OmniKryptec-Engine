package omnikryptec.ppstages;

import java.util.List;

import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.postprocessing.FrameBufferObject;
import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.postprocessing.PostProcessingStage;

public class CompleteGaussianBlurStage implements PostProcessingStage{
	
	private SingleGaussianBlurStage hb,vb;
	private FrameBufferObject tmp;
	
	
	public CompleteGaussianBlurStage(boolean scalefbo, float wm, float hm){
		hb = new SingleGaussianBlurStage(true, wm, hm, scalefbo);
		vb = new SingleGaussianBlurStage(false, wm, hm, scalefbo);
	}

	
	public void setListIndex(int i){
		hb.setListIndex(i);
	}
	
	
	@Override
	public void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage) {	
		hb.render(before, beforelist, stage);
		tmp = hb.getFbo();
		if(tmp.getDepthbufferType()==DepthbufferType.NONE){
			Logger.log("FBO of H-Blur Stage ("+stage+" (zerobased)) has no Depthbufferattachment. Some PostProcessingStages may not work anymore.", LogLevel.WARNING);
		}
		before.resolveDepth(tmp);
		before = tmp;
		beforelist.add(before);
		vb.render(before, beforelist, stage);
	}

	@Override
	public FrameBufferObject getFbo() {
		return vb.getFbo();
	}

	@Override
	public void resize() {
		hb.resize();
		vb.resize();
	}
	
}
