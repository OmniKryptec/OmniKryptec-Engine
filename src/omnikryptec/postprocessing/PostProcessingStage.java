package omnikryptec.postprocessing;

import java.util.List;

public interface PostProcessingStage {

	void render(FrameBufferObject before, List<FrameBufferObject> beforelist);

	FrameBufferObject getFbo();
	
	default boolean usesDefaultRenderObject(){
		return true;
	}
	
}
