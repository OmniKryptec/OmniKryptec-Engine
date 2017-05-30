package omnikryptec.ppstages;

import java.util.List;

import org.lwjgl.opengl.Display;

import omnikryptec.postprocessing.FrameBufferObject;
import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.postprocessing.PostProcessingStep;
import omnikryptec.shader.Shader;
import omnikryptec.shader_files.FogShader;

public class FogStage extends PostProcessingStep {
	
	private static FogShader shader = new FogShader();
	
	public FogStage() {
		super(shader);
	}

	@Override
	public void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using, int stage) {
		beforelist.get(0).bindToUnit(0);
		before.bindDepthTexture(1);
	}

	@Override
	public void afterRendering() {

	}

	@Override
	public FrameBufferObject getOnResize() {
		return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	}

}
