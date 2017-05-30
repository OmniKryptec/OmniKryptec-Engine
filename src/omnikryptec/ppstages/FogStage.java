package omnikryptec.ppstages;

import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector4f;

import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.postprocessing.FrameBufferObject;
import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.postprocessing.PostProcessingStep;
import omnikryptec.shader.Shader;
import omnikryptec.shader_files.FogShader;

public class FogStage extends PostProcessingStep {
	
	private static FogShader shader = new FogShader();
	
	private Vector4f fog = new Vector4f(0.3f,0.3f,0.3f,1);
	
	private float density=0.007f;
	private float gradient=1.5f;
	
	public FogStage() {
		super(shader);
	}

	
	public FogStage setFog(Vector4f v){
		return setFog(v.x, v.y, v.z, v.w);
	}
	
	public FogStage setFog(float r, float g, float b, float a){
		fog.set(r, g, b, a);
		return this;
	}
	
	public FogStage setDensity(float d){
		density = d;
		return this;
	}
	
	public FogStage setGradient(float g){
		gradient = g;
		return this;
	}
	
	@Override
	public void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using, int stage) {
		FogShader.test.loadVec2(OmniKryptecEngine.instance().getCurrentScene().getCamera().getPlanesForLR());
		FogShader.fog.loadVec4(fog);
		FogShader.density.loadFloat(density);
		FogShader.gradient.loadFloat(gradient);
		before.bindToUnit(0);
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
