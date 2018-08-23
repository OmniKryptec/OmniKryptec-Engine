package de.omnikryptec.postprocessing.v2;

import de.omnikryptec.postprocessing.main.FrameBufferObject;
import de.omnikryptec.resource.texture.Texture;

import java.util.ArrayList;
import java.util.List;

public class PostProcessor extends PostProcessingTask {

	private List<PostProcessingTask> tasks = new ArrayList<>();
	private PostProcessor parent;
	private FrameBufferObject input;

	public PostProcessor() {
		this(null);
	}

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

	public FrameBufferObject getInput() {
		return input;
	}
	
	public Texture getDepthTexture() {
		if (parent != null) {
			return parent.getDepthTexture();
		}
		if (input == null) {
			return null;
		}
		return input.getDepthTexture();
	}

	@Override
	public FrameBufferObject process(PostProcessor parent, FrameBufferObject texToProcess) {
		this.parent = parent;
		for (PostProcessingTask t : tasks) {
			if (t.isEnabled()) {
				texToProcess = t.process(this, texToProcess);
			}
		}
		return texToProcess;
	}

}
