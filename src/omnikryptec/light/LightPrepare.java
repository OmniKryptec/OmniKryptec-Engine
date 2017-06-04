package omnikryptec.light;

import omnikryptec.main.Scene;
import omnikryptec.shader.Shader;

public interface LightPrepare {
	
	public static final LightPrepare DEFAULT_LIGHT_PREPARE = new DefaultLightPrepare();
	
	
	Shader getShader();
	void prepare(Scene cur);
	void prepareLight(Light l);
	
}
