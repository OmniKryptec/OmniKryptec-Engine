package omnikryptec.postprocessing;

import java.util.List;

public interface PostProcessingStage {

	public void render(Fbo before, List<Fbo> beforelist);

	public Fbo getFbo();

}
