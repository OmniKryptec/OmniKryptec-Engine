package omnikryptec.postprocessing;

import java.util.List;

public interface PostProcessingStage {
	
	public static final int INDEX_OPTION_USE_LAST_FBO = -1;
	
	/**
	 * 
	 * @param before
	 * @param beforelist
	 * @param stage index of current PostProcessingStage (0-based)
	 */
	void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage);

	FrameBufferObject getFbo();
	
	void resize();
	
	
	default boolean usesDefaultRenderObject(){
		return true;
	}
		
}
