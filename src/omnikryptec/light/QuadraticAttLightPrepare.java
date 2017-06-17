package omnikryptec.light;

import org.lwjgl.opengl.Display;

import omnikryptec.main.Scene;
import omnikryptec.shader.Shader;
import omnikryptec.shader_files.QuadLightShader;

public class QuadraticAttLightPrepare implements LightPrepare {

	private QuadLightShader shader = new QuadLightShader();

	@Override
	public Shader getShader() {
		return shader;
	}

	@Override
	public void prepare(Scene cur) {
		shader.viewv.loadMatrix(cur.getCamera().getViewMatrix());
		shader.proj.loadMatrix(cur.getCamera().getProjectionMatrix());
		shader.pixSizes.loadVec2(1.0f / Display.getWidth(), 1.0f / Display.getHeight());
	}

	@Override
	public void prepareLight(Light l) {
		shader.light.loadVec4(l.getPosRad());
		shader.lightColor.loadVec3(l.getColor());
		shader.att.loadVec3(l.getAttenuation());
	}

}
