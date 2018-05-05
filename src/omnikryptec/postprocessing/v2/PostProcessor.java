package omnikryptec.postprocessing.v2;

import java.util.ArrayList;
import java.util.List;

import omnikryptec.postprocessing.main.FrameBufferObject;
import omnikryptec.resource.texture.Texture;

public class PostProcessor extends PostProcessingTask {

	private List<PostProcessingTask> tasks = new ArrayList<>();
	private PostProcessor parent;
	private FrameBufferObject input;
	
	public PostProcessor(FrameBufferObject input) {
		this.input = input;
	}
	
	public PostProcessor addTask(PostProcessingTask task) {
		tasks.add(task);
		return this;
	}

	public PostProcessor removeTask(PostProcessingTask task) {
		tasks.remove(task);
		return this;
	}

	public List<PostProcessingTask> getTasks() {
		return tasks;
	}

	public Texture getDepthTexture() {
		if (parent != null) {
			return parent.getDepthTexture();
		}
		return input.getDepthTexture();
	}

	@Override
	public Texture process(PostProcessor parent, Texture texToProcess) {
		this.parent = parent;
		for(PostProcessingTask t : tasks) {
			if(t.isEnabled()) {
				texToProcess = t.process(this, texToProcess);
			}
		}
		return texToProcess;
	}

}
