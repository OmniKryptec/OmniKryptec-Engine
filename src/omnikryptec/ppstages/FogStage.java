package omnikryptec.ppstages;

import java.util.List;

import org.joml.Vector4f;

import omnikryptec.display.Display;
import omnikryptec.entity.Camera;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.postprocessing.FrameBufferObject;
import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.postprocessing.PostProcessingStep;
import omnikryptec.shader.Shader;
import omnikryptec.shader_files.FogShader;

public class FogStage extends PostProcessingStep {

	private static FogShader shader = new FogShader();

	private Vector4f fog = new Vector4f(0.3f, 0.3f, 0.3f, 1);

	private float density = 0.007f;
	private float gradient = 1.5f;

	public FogStage() {
		super(shader);
	}

	public FogStage setFog(Vector4f v) {
		return setFog(v.x, v.y, v.z, v.w);
	}

	public FogStage setFog(float r, float g, float b, float a) {
		fog.set(r, g, b, a);
		return this;
	}

	public FogStage setDensity(float d) {
		density = d;
		return this;
	}

	public FogStage setGradient(float g) {
		gradient = g;
		return this;
	}

	private int[] l_ind = { -1, -1 };

	public FogStage setListIndices(int diff, int depth) {
		l_ind[0] = diff;
		l_ind[1] = depth;
		return this;
	}

	private Camera curcam;

	@Override
	public void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using, int stage) {
		shader.pixsize.loadVec2(1.0f / Display.getWidth(), 1.0f / Display.getHeight());
		shader.fog.loadVec4(fog);
		shader.density.loadFloat(density);
		shader.gradient.loadFloat(gradient);
		curcam = OmniKryptecEngine.instance().getCurrentScene().getCamera();
		shader.invprojv.loadMatrix(curcam.getProjectionViewMatrix().invert());
		shader.campos.loadVec3(curcam.getAbsolutePos());
		(l_ind[0] < 0 ? before : beforelist.get(l_ind[0])).bindToUnit(0);
		(l_ind[1] < 0 ? before : beforelist.get(l_ind[1])).bindDepthTexture(1);
	}

	@Override
	public FrameBufferObject createFbo() {
		return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	}

}
