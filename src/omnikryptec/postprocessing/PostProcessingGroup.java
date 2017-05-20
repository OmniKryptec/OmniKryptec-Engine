package omnikryptec.postprocessing;

import java.util.ArrayList;
import java.util.List;

public class PostProcessingGroup implements PostProcessingStage {

	private FrameBufferObject fbo;
	private PostProcessingStage[] stages;
	
	public PostProcessingGroup(FrameBufferObject target, PostProcessingStage...stages){
		this.fbo = target;
		this.stages = stages;
	}
	
	@Override
	public void render(FrameBufferObject before, List<FrameBufferObject> beforelist) {
		List<FrameBufferObject> beforel = new ArrayList<>();
		beforel.add(before);
		for(int i=0; i<stages.length; i++){
			stages[i].render(before, beforel);
			before = stages[i].getFbo();
			beforel.add(before);
		}
	}

	@Override
	public FrameBufferObject getFbo() {
		return fbo;
	}

}
