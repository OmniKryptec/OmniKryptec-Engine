package de.omnikryptec.main;

public class DefaultGameLoop extends GameLoop {

	public static final int MODE_2D = 0x1;
	public static final int MODE_3D = 0x2;
	public static final int MODE_GUI = 0x4;
	public static final int MODE_PP = 0x8;
	public static final int MODE_GL_TASKS = 0x10;

	private int mode = MODE_2D | MODE_3D | MODE_GUI | MODE_PP | MODE_GL_TASKS;

	public void setMode(int mode) {
		this.mode = mode;
	}

	public DefaultGameLoop addMode(int mode) {
		this.mode |= mode;
		return this;
	}

	public DefaultGameLoop removeMode(int mode) {
		this.mode &= ~mode;
		return this;
	}

	@Override
	protected void runLoop() {
		while (!isStopRequested()) {
			step();
		}
	}

	@Override
	protected void runStep() {
		sleepIfInactive();
		updateAudio();
		checkAndDealWithResized();
		beginScenesRendering();
		clear();
		if ((mode & MODE_3D) != 0) {
			render3D();
			logic3D();
		}
		if ((mode & MODE_2D) != 0) {
			render2D();
			logic2D();
		}
		if ((mode & MODE_GL_TASKS) != 0) {
			doGLTasks(-1);
		}
		endScenesRendering();
		sceneToScreen((mode & MODE_PP) != 0);
		if ((mode & MODE_GUI) != 0) {
			renderGui();
		}
		refresh();
	}

	@Override
	public float getDeltaTimef() {
		return engineInstance.getDisplayManager().getDUDeltaTimef();
	}
}
