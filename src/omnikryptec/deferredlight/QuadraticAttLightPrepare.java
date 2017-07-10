package omnikryptec.deferredlight;

import omnikryptec.display.Display;
import omnikryptec.gameobject.Light;
import omnikryptec.main.Scene;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.files.QuadLightShader;

public class QuadraticAttLightPrepare implements DeferredLightPrepare {

//	private QuadLightShader shader = new QuadLightShader();
//
//	@Override
//	public Shader getShader() {
//		return shader;
//	}
//
//	@Override
//	public void prepare(Scene cur) {
//		shader.viewv.loadMatrix(cur.getCamera().getViewMatrix());
//		shader.proj.loadMatrix(cur.getCamera().getProjectionMatrix());
//		shader.pixSizes.loadVec2(1.0f / Display.getWidth(), 1.0f / Display.getHeight());
//	}
//
//	@Override
//	public void prepareLight(Light l) {
//		shader.light.loadVec3(l.getPosition());
//		shader.lightColor.loadVec3(l.getColor().getArray());
//		//shader.att.loadVec3(l.getAttenuation());
//	}

}
