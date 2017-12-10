package omnikryptec.main;

import omnikryptec.graphics.GraphicsUtil;

public class DefaultGameLoop extends GameLoop{

	@Override
	protected void runLoop() {
		while(!isStopRequested()) {
			runStep();
		}
	}

	@Override
	protected void runStep() {
		sleepIfInactive();
		updateAudio();
		checkAndDealWithResized();
		beginScenesRendering();
		clear();
		render3D();
		logic3D();
		render2D();
		logic2D();
		doGLTasks(-1);
		endScenesRendering();
		doPostprocessing();
		renderGui();
		refresh();
	}

	@Override
	public float getDeltaTimef() {
		return engineInstance.getDisplayManager().getDUDeltaTimef();
	}
}
