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

import java.util.List;

import de.omnikryptec.old.postprocessing.main.FrameBufferObject;
import de.omnikryptec.old.postprocessing.main.PostProcessingStage;

public class CompleteGaussianBlurStage extends PostProcessingStage {

	private SingleGaussianBlurStage hb, vb;

	public CompleteGaussianBlurStage(boolean scalefbo, float wm, float hm) {
		hb = new SingleGaussianBlurStage(true, wm, hm, scalefbo);
		vb = new SingleGaussianBlurStage(false, wm, hm, scalefbo);
	}

	public void setListIndex(int i) {
		hb.setListIndex(i);
	}

	@Override
	public void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage) {
		hb.renderAndResolveDepth(before, beforelist, stage);
		vb.renderAndResolveDepth(hb.getFbo(), beforelist, stage);
	}

	@Override
	public FrameBufferObject getFbo() {
		return vb.getFbo();
	}

	@Override
	public void onResize() {
		hb.resize();
		vb.resize();
	}

	@Override
	protected FrameBufferObject createFbo() {
		return null;
	}

}
