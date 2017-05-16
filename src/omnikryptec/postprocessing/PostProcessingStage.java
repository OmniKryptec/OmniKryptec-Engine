package omnikryptec.postprocessing;

import java.util.List;

public interface PostProcessingStage {

	void render(Fbo before, List<Fbo> beforelist);

	Fbo getFbo();
	
	default boolean usesDefaultRenderObject(){
		return true;
	}
	
}
