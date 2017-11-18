package omnikryptec.main;

import omnikryptec.display.Display;

public class DefaultGameLoop extends GameLoop{

	@Override
	protected void runLoop() {
		while(!isStopRequested()) {
			renderOneFrame();
		}
	}

	@Override
	protected void renderOneFrame() {
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
	public float getDeltaTime() {
		return 0;
	}


}
