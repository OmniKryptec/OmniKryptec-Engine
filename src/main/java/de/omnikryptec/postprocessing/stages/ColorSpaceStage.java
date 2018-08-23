package de.omnikryptec.postprocessing.stages;

import de.omnikryptec.display.Display;
import de.omnikryptec.postprocessing.main.FrameBufferObject;
import de.omnikryptec.postprocessing.main.PostProcessingStageShaded;
import de.omnikryptec.shader.base.Shader;
import de.omnikryptec.shader.files.postprocessing.ColorSpaceShader;
import de.omnikryptec.util.EnumCollection.DepthbufferType;
import org.joml.Vector3f;

import java.util.List;

public class ColorSpaceStage extends PostProcessingStageShaded {

	private static ColorSpaceShader shader = new ColorSpaceShader();
	private Vector3f level = new Vector3f();

	public ColorSpaceStage() {
		this(256);
	}

	public ColorSpaceStage(float rgb) {
		this(rgb, rgb, rgb);
	}

	public ColorSpaceStage(float r, float g, float b) {
		super(shader);
		this.level.x = r;
		this.level.y = g;
		this.level.z = b;
	}

	private int ind = -1;

	public ColorSpaceStage setListIndex(int i) {
		ind = i;
		return this;
	}

	@Override
	public void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using, int stage) {
		ColorSpaceShader.value.loadVec3(level);
		if (ind < 0) {
			before.bindToUnitOptimized(0);
		} else {
			beforelist.get(ind).bindToUnitOptimized(0);
		}
	}

	@Override
	public FrameBufferObject createFbo() {
		return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	}

	public ColorSpaceStage setLevels(float rgb) {
		return setLevels(rgb, rgb, rgb);
	}

	public ColorSpaceStage setLevels(float r, float g, float b) {
		level.x = r;
		level.y = g;
		level.z = b;
		return this;
	}

	public ColorSpaceStage setLevels(Vector3f l) {
		this.level = l;
		return this;
	}

	public Vector3f getLevels() {
		return level;
	}

}
