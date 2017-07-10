package omnikryptec.deferredlight;

import omnikryptec.display.Display;
import omnikryptec.gameobject.Light;
import omnikryptec.main.Scene;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.files.LightShader;

public class DefaultDeferredLightPrepare implements DeferredLightPrepare {

//	private LightShader shader = new LightShader();
//
//	@Override
//	public void prepare(Scene cur) {
//		shader.invprojv.loadMatrix(cur.getCamera().getInverseProjView());
//		shader.pixSizes.loadVec2(1.0f / Display.getWidth(), 1.0f / Display.getHeight());
//		shader.cam.loadVec3(cur.getCamera().getPosition());
//	}
//
//	@Override
//	public void prepareLight(Light l) {
//		shader.light.loadVec3(l.getPosition());
//		shader.lightColor.loadVec3(l.getColor().getArray());
//	}
//
//	@Override
//	public Shader getShader() {
//		return shader;
//	}

}
