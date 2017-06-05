package omnikryptec.ppstages;

import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector4f;

import omnikryptec.postprocessing.FrameBufferObject;
import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.postprocessing.PostProcessingStep;
import omnikryptec.shader.Shader;
import omnikryptec.shader_files.BrightnessfilterShader;

public class BrightnessfilterStage extends PostProcessingStep {
	
	private BrightnessfilterShader shader;
	
	public BrightnessfilterStage(Vector4f extrainfo){
		shader = new BrightnessfilterShader(extrainfo);
		setShader(shader);
	}
	
	private int[] l_ind = {-1, 3};
	
	public BrightnessfilterStage setListIndices(int before, int extra){
		l_ind[0] = before;
		l_ind[1] = extra;
		return this;
	}
	
	@Override
	public void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using, int stage) {
		(l_ind[0]<0?before:beforelist.get(l_ind[0])).bindToUnit(0);
		(l_ind[1]<0?before:beforelist.get(l_ind[1])).bindToUnit(1);
	}

	@Override
	public void afterRendering() {

	}

	@Override
	public FrameBufferObject getOnResize() {
		return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	}

}
