package omnikryptec.main;

public class DefaultGameLoop extends GameLoop{

	@Override
	protected void runLoop() {
		while(!isStopRequested()) {
			step();
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
}
