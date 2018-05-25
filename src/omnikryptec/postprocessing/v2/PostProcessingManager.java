package omnikryptec.postprocessing.v2;

import java.util.HashMap;
import java.util.Map;

import omnikryptec.graphics.GraphicsUtil;
import omnikryptec.resource.model.Model;
import omnikryptec.util.ModelUtil;

public class PostProcessingManager {
	
	private Model quad = ModelUtil.generateQuad();
	private Map<Integer, PostProcessor> extra = new HashMap<>();
	
	public void execute() {
		start();
		
		end();
	}
	
	private void start() {
		quad.getVao().bind(0, 1);
		GraphicsUtil.enableDepthTesting(false);
	}

	private void end() {
		GraphicsUtil.enableDepthTesting(true);
	}
}
