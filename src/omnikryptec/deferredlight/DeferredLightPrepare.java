package omnikryptec.deferredlight;

import omnikryptec.gameobject.gameobject.Light;
import omnikryptec.main.Scene;
import omnikryptec.shader.base.Shader;

public interface DeferredLightPrepare {

	public static final DeferredLightPrepare FORWARD_RENDERING = null;
	public static final DeferredLightPrepare DEFAULT_LIGHT_PREPARE = new DefaultDeferredLightPrepare();
	public static final DeferredLightPrepare ATT_LIGHT_PREPARE = new QuadraticAttLightPrepare();

	Shader getShader();

	void prepare(Scene cur);

	void prepareLight(Light l);

}
