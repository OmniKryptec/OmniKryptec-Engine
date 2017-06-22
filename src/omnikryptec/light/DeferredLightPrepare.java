package omnikryptec.light;

import omnikryptec.main.Scene;
import omnikryptec.shader.Shader;

public interface DeferredLightPrepare {

	public static final DeferredLightPrepare DEFAULT_LIGHT_PREPARE = new DefaultLightPrepare();
	public static final DeferredLightPrepare ATT_LIGHT_PREPARE = new QuadraticAttLightPrepare();

	Shader getShader();

	void prepare(Scene cur);

	void prepareLight(Light l);

}
