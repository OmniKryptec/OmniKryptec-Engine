package omnikryptec.postprocessing;

import java.util.List;

import org.lwjgl.opengl.GL11;

import omnikryptec.util.RenderUtil;

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
	
	
	default void renderQuad(boolean clear){
		if(clear){
			RenderUtil.clear(0, 0, 0, 0);
		}
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
	}
		
}
