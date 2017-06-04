package omnikryptec.light;

import omnikryptec.main.Scene;
import omnikryptec.shader.Shader;
import omnikryptec.shader_files.QuadLightShader;

public class QuadraticAttLightPrepare implements LightPrepare {

	private QuadLightShader shader = new QuadLightShader();
	
	@Override
	public Shader getShader() {
		return null;
	}

	@Override
	public void prepare(Scene cur) {
		
	}

	@Override
	public void prepareLight(Light l) {

	}

}
