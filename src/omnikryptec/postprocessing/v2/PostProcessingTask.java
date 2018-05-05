package omnikryptec.postprocessing.v2;

import omnikryptec.resource.texture.Texture;

public abstract class PostProcessingTask {

	private boolean enabled=true;
	
	public PostProcessingTask setEnabled(boolean b) {
		this.enabled = b;
		return this;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public abstract Texture process(PostProcessor parent, Texture texToProcess);
	
}
