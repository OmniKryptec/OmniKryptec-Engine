package omnikryptec.deferredlight;

import org.lwjgl.opengl.Display;

import omnikryptec.entity.Light;
import omnikryptec.main.Scene;
import omnikryptec.shader.Shader;
import omnikryptec.shader_files.LightShader;

public class DefaultDeferredLightPrepare implements DeferredLightPrepare {

	private LightShader shader = new LightShader();

	@Override
	public void prepare(Scene cur) {
		shader.invprojv.loadMatrix(cur.getCamera().getInverseProjView());
		shader.pixSizes.loadVec2(1.0f / Display.getWidth(), 1.0f / Display.getHeight());
		shader.cam.loadVec3(cur.getCamera().getAbsolutePos());
	}

	@Override
	public void prepareLight(Light l) {
		shader.light.loadVec3(l.getPosRad());
		shader.lightColor.loadVec3(l.getColor().getArray());
	}

	@Override
	public Shader getShader() {
		return shader;
	}

}
