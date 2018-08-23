package de.omnikryptec.postprocessing.stages;

import de.omnikryptec.display.Display;
import de.omnikryptec.postprocessing.main.FrameBufferObject;
import de.omnikryptec.postprocessing.main.PostProcessingStageShaded;
import de.omnikryptec.shader.base.Shader;
import de.omnikryptec.shader.files.postprocessing.ContrastchangeShader;
import de.omnikryptec.util.EnumCollection.DepthbufferType;

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
