package omnikryptec.postprocessing.v2;

import omnikryptec.graphics.GraphicsUtil;
import omnikryptec.postprocessing.main.FrameBufferObject;
import omnikryptec.resource.model.Model;
import omnikryptec.util.ModelUtil;

public abstract class PostProcessingTask {

	private boolean enabled=true;
	//private boolean simplequad=true;
	
	public PostProcessingTask setEnabled(boolean b) {
		this.enabled = b;
		return this;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	
	
//	public FrameBufferObject process(PostProcessor parent, FrameBufferObject toprocess) {
//		if(!s)
//	}
	
	public abstract FrameBufferObject process(PostProcessor parent, FrameBufferObject texToProcess);
	
}
