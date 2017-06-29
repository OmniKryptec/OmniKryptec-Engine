package omnikryptec.deferredlight;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import omnikryptec.display.Display;
import omnikryptec.display.DisplayManager;
import omnikryptec.entity.Light;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene;
import omnikryptec.postprocessing.FrameBufferObject;
import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.postprocessing.PostProcessingStage;
import omnikryptec.util.RenderUtil;

public class DeferredLightStage extends PostProcessingStage {

	private DeferredLightPrepare[] preparea;

	public DeferredLightStage() {
		this(DeferredLightPrepare.DEFAULT_LIGHT_PREPARE);
	}

	public DeferredLightStage(DeferredLightPrepare... prepares) {
		preparea = prepares;
	}

	private int[] l_ind = { 0, 1, 2 };
	private boolean[] u_list = { false, true, true };

	@Override
	public void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage) {
		if (!DisplayManager.instance().getSettings().isLightDeferredAllowed() && Logger.isDebugMode()) {
			Logger.log("Deferred light is not enabled. Will not render lights.", LogLevel.WARNING);
			before.resolveToFbo(getFbo(), GL30.GL_COLOR_ATTACHMENT0, false);
			return;
		}
		render(OmniKryptecEngine.instance().getCurrentScene(), u_list[0] ? beforelist.get(l_ind[0]) : before,
				u_list[1] ? beforelist.get(l_ind[1]) : before, u_list[2] ? beforelist.get(l_ind[2]) : before);
	}

	public DeferredLightStage setListIndices(int diffDepth, int normal, int specular) {
		l_ind[0] = diffDepth;
		u_list[0] = diffDepth < 0;
		l_ind[1] = normal;
		u_list[1] = normal < 0;
		l_ind[2] = specular;
		u_list[2] = specular < 0;
		return this;
	}

	private Light l;
	private List<Light> relevant;

	private void render(Scene currentScene, FrameBufferObject unsampledfbo, FrameBufferObject normalfbo,
			FrameBufferObject specularfbo) {
		RenderUtil.enableAdditiveBlending();
		getFbo().bindFrameBuffer();
		RenderUtil.clear(0, 0, 0, 1);
		for (int i = 0; i < preparea.length; i++) {
			preparea[i].getShader().start();
			unsampledfbo.bindToUnit(0, 0);
			normalfbo.bindToUnit(1, 0);
			specularfbo.bindToUnit(2, 0);
			unsampledfbo.bindDepthTexture(3);
			preparea[i].prepare(currentScene);
			//relevant = currentScene.getDeferredRenderLights();
			if (relevant != null) {
				for (int j = 0; j < relevant.size(); j++) {
					l = relevant.get(j);
					if (l.isActive()) {
						l.doLogic0();
						preparea[i].prepareLight(l);
						GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
					}
				}
			}
		}
		getFbo().unbindFrameBuffer();
		RenderUtil.disableBlending();

	}

	@Override
	public FrameBufferObject createFbo() {
		return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	}

}
