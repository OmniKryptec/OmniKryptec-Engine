package de.omnikryptec.postprocessing.v2;

import de.omnikryptec.graphics.GraphicsUtil;
import de.omnikryptec.resource.model.Model;
import de.omnikryptec.util.ModelUtil;

import java.util.HashMap;
import java.util.Map;

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
