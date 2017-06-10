package omnikryptec.ppstages;

import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector4f;

import omnikryptec.postprocessing.FrameBufferObject;
import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.postprocessing.PostProcessingStep;
import omnikryptec.shader.Shader;
import omnikryptec.shader_files.FilterShader;

public class FilterStage extends PostProcessingStep {

	private FilterShader shader = new FilterShader();

	private Vector4f channels = new Vector4f();

	private int[] l_ind = { -1, 3 };

	public FilterStage(float r, float g, float b, float a) {
		setShader(shader);
		channels.set(r, g, b, a);
	}

	public FilterStage(Vector4f rgba) {
		this(rgba.x, rgba.y, rgba.z, rgba.w);
	}

	public FilterStage setListIndices(int before, int extra) {
		l_ind[0] = before;
		l_ind[1] = extra;
		return this;
	}

	@Override
	public void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using, int stage) {
		(l_ind[0] < 0 ? before : beforelist.get(l_ind[0])).bindToUnit(0);
		(l_ind[1] < 0 ? before : beforelist.get(l_ind[1])).bindToUnit(1);
		shader.channels.loadVec4(channels);
	}

	@Override
	protected FrameBufferObject createFbo() {
		return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	}

}