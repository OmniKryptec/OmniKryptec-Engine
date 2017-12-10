package omnikryptec.postprocessing.stages;

import java.util.List;

import omnikryptec.display.Display;
import omnikryptec.postprocessing.main.FrameBufferObject;
import omnikryptec.postprocessing.main.PostProcessingStep;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.files.postprocessing.ContrastchangeShader;
import omnikryptec.util.EnumCollection.DepthbufferType;

public class ContrastchangeStage extends PostProcessingStep {

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
