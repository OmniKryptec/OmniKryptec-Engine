/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.postprocessing.v2;

import de.omnikryptec.old.postprocessing.main.FrameBufferObject;
import de.omnikryptec.old.resource.texture.Texture;

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
