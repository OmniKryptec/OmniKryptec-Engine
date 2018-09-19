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

import de.omnikryptec.old.display.Display;
import de.omnikryptec.old.postprocessing.main.FrameBufferObject;
import de.omnikryptec.old.postprocessing.main.PostProcessingStageShaded;
import de.omnikryptec.old.shader.base.Shader;
import de.omnikryptec.old.shader.files.postprocessing.GaussianBlurShader;
import de.omnikryptec.old.util.EnumCollection.DepthbufferType;

import java.util.List;

public class SingleGaussianBlurStage extends PostProcessingStageShaded {

	private static GaussianBlurShader shader = new GaussianBlurShader("gaussian_blur_vert.glsl");
	private boolean isHorizontal, scalefbo;
	private float w, h;

	public SingleGaussianBlurStage(boolean ishor, float widthmult, float heightmult, boolean scaleFbo) {
		super(shader);
		this.isHorizontal = ishor;
		w = widthmult;
		h = heightmult;
		scalefbo = scaleFbo;
	}

	private int l_ind = -1;

	public SingleGaussianBlurStage setListIndex(int i) {
		l_ind = i;
		return this;
	}

	@Override
	public void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using, int stage) {
		GaussianBlurShader.isHor.loadBoolean(isHorizontal);
		GaussianBlurShader.size.loadFloat(isHorizontal ? Display.getWidth() * w : Display.getHeight() * h);
		(l_ind < 0 ? before : beforelist.get(l_ind)).bindToUnitOptimized(0);
	}

	@Override
	public FrameBufferObject createFbo() {
		return new FrameBufferObject((int) (Display.getWidth() * (scalefbo ? w : 1)),
				(int) (Display.getHeight() * (scalefbo ? h : 1)), DepthbufferType.DEPTH_TEXTURE);
	}

}
