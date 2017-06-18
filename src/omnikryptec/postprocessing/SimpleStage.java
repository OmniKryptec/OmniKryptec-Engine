package omnikryptec.postprocessing;

import java.util.List;

import org.lwjgl.opengl.Display;

import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.shader.Shader;

public class SimpleStage extends PostProcessingStep {

	public SimpleStage(Shader shader) {
		setShader(shader);
	}

	private int l_ind = -1;

	public SimpleStage setListIndex(int i) {
		l_ind = i;
		return this;
	}

	@Override
	public void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using, int stage) {
		(l_ind < 0 ? before : beforelist.get(l_ind)).bindToUnita(0);
	}

	@Override
	protected FrameBufferObject createFbo() {
		return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	}

}
