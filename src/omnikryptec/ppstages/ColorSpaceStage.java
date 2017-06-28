package omnikryptec.ppstages;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import omnikryptec.display.Display;
import omnikryptec.postprocessing.FrameBufferObject;
import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.postprocessing.PostProcessingStep;
import omnikryptec.shader.Shader;
import omnikryptec.shader_files.ColorSpaceShader;

public class ColorSpaceStage extends PostProcessingStep {

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
			before.bindToUnit(0);
		} else {
			beforelist.get(ind).bindToUnit(0);
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
