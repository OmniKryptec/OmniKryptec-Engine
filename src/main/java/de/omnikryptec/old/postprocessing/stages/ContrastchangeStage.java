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

package de.omnikryptec.old.postprocessing.stages;

import de.omnikryptec.core.display.Display;
import de.omnikryptec.old.postprocessing.main.FrameBufferObject;
import de.omnikryptec.old.postprocessing.main.PostProcessingStageShaded;
import de.omnikryptec.old.shader.base.Shader;
import de.omnikryptec.old.shader.files.postprocessing.ContrastchangeShader;
import de.omnikryptec.old.util.EnumCollection.DepthbufferType;

import java.util.List;

public class ContrastchangeStage extends PostProcessingStageShaded {

	private static ContrastchangeShader shader = new ContrastchangeShader();
	private float change = 0;

	public ContrastchangeStage() {
		this(0);
	}

	public ContrastchangeStage(float change) {
		super(shader);
		this.change = change;
	}

	public float getChange() {
		return change;
	}

	public ContrastchangeStage setChange(float f) {
		this.change = f;
		return this;
	}

	private int list_ind = -1;

	public ContrastchangeStage setListIndex(int beforeI) {
		list_ind = beforeI;
		return this;
	}

	@Override
	public void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using, int stage) {
		if (list_ind < 0) {
			before.bindToUnitOptimized(0);
		} else {
			beforelist.get(list_ind).bindToUnitOptimized(0);
		}
		ContrastchangeShader.change.loadFloat(change);
	}

	@Override
	public FrameBufferObject createFbo() {
		return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	}

}
