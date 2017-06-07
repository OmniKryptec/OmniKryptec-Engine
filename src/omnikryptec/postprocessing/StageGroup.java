package omnikryptec.postprocessing;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL30;

import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;

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
