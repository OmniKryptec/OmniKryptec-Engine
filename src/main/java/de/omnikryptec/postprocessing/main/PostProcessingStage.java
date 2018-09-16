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

import java.util.List;

import org.lwjgl.opengl.GL11;

import de.omnikryptec.graphics.GraphicsUtil;

public abstract class PostProcessingStage {

	public static final int INDEX_OPTION_USE_LAST_FBO = -1;

	private boolean enabled = true;
	private FrameBufferObject target;

	public PostProcessingStage() {
		target = createFbo();
	}

	public PostProcessingStage setEnabled(boolean b) {
		this.enabled = b;
		return this;
	}

	public boolean isEnabled() {
		return enabled;
	}

	protected void renderQuad(boolean clear) {
		target.bindFrameBuffer();
		if (clear) {
			GraphicsUtil.clear(0, 0, 0, 1);
		}
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 6);
		target.unbindFrameBuffer();
	}

	public FrameBufferObject getFbo() {
		return target;
	}

	public PostProcessingStage setDepthbuffer(FrameBufferObject fbo) {
		if (target != null) {
			fbo.resolveDepth(target);
		}
		return this;
	}

	public final void resize() {
		if(target!=null){
			target.delete();
		}
		target = createFbo();
		onResize();
	}

	public final void renderAndResolveDepth(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage) {
		render(before, beforelist, stage);
		setDepthbuffer(before);
	}

	/**
	 * 
	 * @param before
	 * @param beforelist
	 * @param stage
	 *            index of current PostProcessingStage (0-based)
	 */
	public abstract void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage);

	protected abstract FrameBufferObject createFbo();

	protected boolean usesDefaultRenderObject() {
		return true;
	}

	protected void onResize() {
	}



}
