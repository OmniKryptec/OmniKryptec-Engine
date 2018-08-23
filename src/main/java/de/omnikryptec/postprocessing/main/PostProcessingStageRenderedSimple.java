package de.omnikryptec.postprocessing.main;

import de.omnikryptec.display.Display;
import de.omnikryptec.shader.base.Shader;
import de.omnikryptec.util.EnumCollection.DepthbufferType;

import java.util.List;

public class PostProcessingStageRenderedSimple extends PostProcessingStageShaded {

	public PostProcessingStageRenderedSimple(Shader shader) {
		setShader(shader);
	}

	private int l_ind = -1;

	public PostProcessingStageRenderedSimple setListIndex(int i) {
		l_ind = i;
		return this;
	}

	@Override
	public void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using, int stage) {
		(l_ind < 0 ? before : beforelist.get(l_ind)).bindToUnit(0);
	}

	@Override
	protected FrameBufferObject createFbo() {
		return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	}

}
