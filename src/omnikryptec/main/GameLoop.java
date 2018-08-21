package omnikryptec.main;

import java.util.Vector;

import omnikryptec.audio.AudioManager;
import omnikryptec.display.Display;
import omnikryptec.event.eventV2.engineevents.FrameEvent;
import omnikryptec.event.eventV2.engineevents.FrameEvent.FrameType;
import omnikryptec.event.eventV2.engineevents.ResizeEvent;
import omnikryptec.event.input.InputManager;
import omnikryptec.graphics.GraphicsUtil;
import omnikryptec.resource.texture.Texture;
import omnikryptec.util.EnumCollection.GameLoopShutdownOption;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public abstract class GameLoop {

	private static final int deltasmoothedFrames = 10;

	// Thread-safe
	private Vector<Runnable> gltasks = new Vector<>();

	private GameLoopShutdownOption stopLevel = GameLoopShutdownOption.NOT_NOW;

	protected final OmniKryptecEngine engineInstance;

	private boolean running = false;
	private double frametime = 0, tmptime = 0;

	private float[] deltas = new float[deltasmoothedFrames];
	private float deltaSmoothed=0;
	private int pointer = 0;
	private long myframecount=0;
	
	protected GameLoop() {
		engineInstance = OmniKryptecEngine.instance();
	}

	public void doGLTasks(int max) {
		if (GraphicsUtil.isGLContextAvailable()) {
			while (gltasks.size() > 0 && (max != 0)) {
				gltasks.firstElement().run();
				gltasks.removeElementAt(0);
				max--;
			}
		} else if (Logger.isDebugMode()) {
			Logger.log("No current context to run doGLTasks!", LogLevel.WARNING);
		}
	}

	final void run() {
		running = true;
		stopLevel = GameLoopShutdownOption.NOT_NOW;
		try {
			runLoop();
		} catch (Exception e) {
			engineInstance.errorOccured(e, "An error occured in the game loop");
		}
		running = false;
		if (Display.isCloseRequested()) {
			engineInstance.shutdown();
		}
	}

	final void step() {
		new FrameEvent(FrameType.PRE).call();
		try {
			deltas[pointer] = getDeltaTimef();
			pointer++;
			pointer %= deltas.length;
			myframecount++;
			int count=0;
			deltaSmoothed = 0;
			for(; count<deltas.length&&count<=myframecount; count++) {
				deltaSmoothed += deltas[count];
			}
			deltaSmoothed/=count;
			tmptime = engineInstance.getDisplayManager().getCurrentTime();
			runStep();
			frametime = engineInstance.getDisplayManager().getCurrentTime() - tmptime;
		} catch (Exception e) {
			engineInstance.errorOccured(e, "An error occured in a step of the game loop");
		}
		new FrameEvent(FrameType.POST).call();
		//engineInstance.getEventsystem().fireEvent(new Event(), EventType.AFTER_FRAME);
	}

	public final double getFrameTime() {
		return frametime;
	}

	public final boolean isRunning() {
		return running;
	}

	public final void requestStop(GameLoopShutdownOption option) {
		stopLevel = option;
	}

	protected final boolean isStopRequested() {
		return stopLevel.getLvl() > 0 || Display.isCloseRequested();
	}

	protected final boolean checkAndDealWithResized() {
		if (Display.wasResized()) {
			engineInstance.refreshFbos();
			engineInstance.getPostprocessor().resize();
			new ResizeEvent(Display.getWidth(), Display.getHeight()).call();
			return true;
		}
		return false;
	}

	protected final void clear() {
		GraphicsUtil.clear(engineInstance.getClearColor());
	}

	protected final void sceneToScreen(boolean pp) {
		engineInstance.sceneToScreen(pp);
	}

	protected final void beginScenesRendering() {
		engineInstance.beginScene3dRendering();
		new FrameEvent(FrameType.STARTSCENE).call();
	}

	protected final void endScenesRendering() {
		new FrameEvent(FrameType.ENDSCENE).call();
		engineInstance.endScene3dRendering();
	}

	protected final void updateAudio() {
		AudioManager.update(engineInstance.getDisplayManager().getCurrentTime());
	}

	protected final void sleepStep() {
		engineInstance.getDisplayManager().updateDisplay();
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			engineInstance.errorOccured(e, "Error occured while sleeping in frame!");
		}
	}

	protected final void sleepIfInactive() {
		while (!Display.isActive()) {
			sleepStep();
		}
	}

	protected final void refresh() {
		InputManager.prePollEvents();
		engineInstance.getDisplayManager().updateDisplay();
		InputManager.nextFrame();
	}

	protected final void render3D() {
		if (engineInstance.getCurrent3DScene() != null) {
			engineInstance.getCurrent3DScene().timedRender();
			engineInstance.getCurrent3DScene().publicParticlesRender();
		}
	}

	protected final void render2D() {
		if (engineInstance.getCurrent2DScene() != null) {
			engineInstance.getCurrent2DScene().timedRender();
		}
	}

	protected final void renderGui() {
		engineInstance.getGuiRenderer().paint();
	}

	protected final void logic3D() {
		if (engineInstance.getCurrent3DScene() != null) {
			engineInstance.getCurrent3DScene().publicParticlesLogic();
			engineInstance.getCurrent3DScene().timedLogic();
		}
	}

	protected final void logic2D() {
		if (engineInstance.getCurrent2DScene() != null) {
			engineInstance.getCurrent2DScene().timedLogic();
		}
	}

	public final float getDeltatimeSmooth() {
		return deltaSmoothed;
	}
	
	protected abstract void runLoop();

	protected abstract void runStep();

	public abstract float getDeltaTimef();
}
