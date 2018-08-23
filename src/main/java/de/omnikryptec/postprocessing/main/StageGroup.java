package de.omnikryptec.postprocessing.main;

import de.omnikryptec.display.Display;
import de.omnikryptec.util.EnumCollection.DepthbufferType;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class StageGroup extends PostProcessingStage {

	private List<PostProcessingStage> stages = new ArrayList<>();

	@Override
	public void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage) {
		for (int i = 0; i < stages.size(); i++) {
			stages.get(i).renderAndResolveDepth(before, beforelist, stage);
			before = stages.get(i).getFbo();
		}
		before.resolveToFbo(getFbo(), GL30.GL_COLOR_ATTACHMENT0);

	}

	@Override
	public FrameBufferObject createFbo() {
		return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	}

	@Override
	protected void onResize() {
		for (int i = 0; i < stages.size(); i++) {
			stages.get(i).resize();
		}
	}
	
}
