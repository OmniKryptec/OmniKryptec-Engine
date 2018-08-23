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

package de.omnikryptec.postprocessing.main;

import de.omnikryptec.display.Display;
import de.omnikryptec.graphics.GraphicsUtil;
import de.omnikryptec.shader.files.postprocessing.DebugShader;
import de.omnikryptec.util.EnumCollection.BlendMode;
import de.omnikryptec.util.EnumCollection.DepthbufferType;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostProcessingDebugStage extends PostProcessingStage {

	private List<Integer> disabled = new ArrayList<>();

	private DebugShader shader = new DebugShader();

	private boolean tmp;
	private int notr;

	public PostProcessingDebugStage() {
	}

	public PostProcessingDebugStage(Integer... is) {
		disabled.addAll(Arrays.asList(is));
	}

	public PostProcessingDebugStage disableIndex(int i) {
		disabled.add(i);
		return this;
	}

	public PostProcessingDebugStage enableIndex(int i) {
		disabled.remove((Integer) i);
		return this;
	}

	private int last = -1;
	private int side = 0;

	@Override
	public void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage) {
		notr = 0;
		shader.start();
		if (last != beforelist.size() + 1 - disabled.size()) {
			side = calcQuadratic(last = beforelist.size() + 1 - disabled.size());
		}
		getFbo().bindFrameBuffer();
		GraphicsUtil.blendMode(BlendMode.ALPHA);
		GraphicsUtil.clear(0, 0, 0, 0);
		if (tmp = !disabled.contains(0)) {
			beforelist.get(0).getDepthTexture().bindToUnitOptimized(0);
			shader.info.loadVec3(side, 0, 0);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 6);
		}
		for (int i = 0; i < beforelist.size(); i++) {
			if (disabled.contains(i + 1)) {
				notr++;
				continue;
			}
			beforelist.get(i).bindToUnitOptimized(0);
			shader.info.loadVec3(side, i + (tmp ? 1 : 0) - notr, (i + (tmp ? 1 : 0) - notr) % side);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 6);
		}
		getFbo().unbindFrameBuffer();
	}

	private int calcQuadratic(int amount) {
		double sqrt = Math.sqrt(amount);
		return (int) Math.ceil(sqrt);
	}

	@Override
	protected FrameBufferObject createFbo() {
		return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	}

}
