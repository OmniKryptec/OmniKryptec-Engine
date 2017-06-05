package omnikryptec.ppstages;

import java.util.List;

import org.lwjgl.opengl.Display;

import omnikryptec.postprocessing.FrameBufferObject;
import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.postprocessing.PostProcessingStep;
import omnikryptec.shader.Shader;
import omnikryptec.shader_files.BrightnessfilterShader;

public class BrightnessfilterStage extends PostProcessingStep {
	
	private BrightnessfilterShader shader = new BrightnessfilterShader();
	
	public BrightnessfilterStage(){
		setShader(shader);
	}
	
	@Override
	public void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using, int stage) {
		before.bindToUnit(0);
		beforelist.get(3).bindToUnit(1);
	}

	@Override
	public void afterRendering() {

	}

	@Override
	public FrameBufferObject getOnResize() {
		return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	}

}
