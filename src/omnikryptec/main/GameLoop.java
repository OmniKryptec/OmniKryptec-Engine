package omnikryptec.main;

import java.util.Vector;

import omnikryptec.audio.AudioManager;
import omnikryptec.display.Display;
import omnikryptec.event.event.Event;
import omnikryptec.event.event.EventType;
import omnikryptec.event.input.InputManager;
import omnikryptec.postprocessing.main.PostProcessing;
import omnikryptec.util.GraphicsUtil;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;
import omnikryptec.util.EnumCollection.GameLoopShutdownOption;

public abstract class GameLoop {
	
	//Thread-safe
	private Vector<Runnable> gltasks = new Vector<>();
	
	private GameLoopShutdownOption stopLevel = GameLoopShutdownOption.NOT_NOW;
	
	protected final OmniKryptecEngine engineInstance;
	
	private boolean running=false;
		
	protected GameLoop() {
		engineInstance = OmniKryptecEngine.instance();
	}
	
	public void doGLTasks(int max) {
		if(GraphicsUtil.isGLContextAvailable()) {
			while(gltasks.size()>0&&(max>0||max==-1)) {
				gltasks.firstElement().run();
				gltasks.removeElementAt(0);
				max--;
			}
		}else if(Logger.isDebugMode()) {
			Logger.log("No current context to run doGLTasks!", LogLevel.WARNING);
		}
	}
	
	public final void run() {
		running = true;
		stopLevel = GameLoopShutdownOption.NOT_NOW;
		try {
			runLoop();
		}catch(Exception e) {
			engineInstance.errorOccured(e, "An error occured in the game loop");
		}
		running = false;
		if(Display.isCloseRequested()) {
			engineInstance.shutdown();
		}
	}
	
	public final void step() {
		
		try {
			renderOneFrame();
		}catch(Exception e) {
			engineInstance.errorOccured(e, "An error occured in a step of the game loop");
		}
		engineInstance.getEventsystem().fireEvent(new Event(), EventType.AFTER_FRAME);
		
	}
	
	public final boolean isRunning() {
		return running;
	}
	
	public final void requestStop(GameLoopShutdownOption option) {
		stopLevel = option;
	}
	
	protected final boolean isStopRequested() {
		return stopLevel.getLvl()>0||Display.isCloseRequested();
	}
	
	protected final boolean checkAndDealWithResized() {
		if (Display.wasResized()) {
			engineInstance.getEventsystem().fireEvent(new Event(), EventType.RESIZED);
			engineInstance.resizeFbos();
			engineInstance.getPostprocessor().resize();
			return true;
		}
		return false;
	}
	
	protected final void clear() {
		GraphicsUtil.clear(engineInstance.getClearColor());
	}
	
	protected final void doPostprocessing() {
		engineInstance.doPostprocessing();
	}
	
	protected final void beginScenesRendering() {
		engineInstance.beginSceneRendering();
	}
	
	protected final void endScenesRendering() {
		engineInstance.getEventsystem().fireEvent(new Event(), EventType.FRAME_EVENT);
		engineInstance.getEventsystem().fireEvent(new Event(), EventType.RENDER_FRAME_EVENT);
		engineInstance.endSceneRendering();
	}
	
	protected final void updateAudio() {
		AudioManager.update(0);
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
		while(!Display.isActive()) {
			sleepStep();
		}
	}
	
	protected final void refresh() {
		InputManager.prePollEvents();
		engineInstance.getDisplayManager().updateDisplay();
		InputManager.nextFrame();
	}
	
	protected final void render3D() {
		engineInstance.getCurrent3DScene().publicRender(null);
	}
	
	protected final void render2D() {
		
	}
	
	protected final void renderGui() {
		
	}
	
	protected final void logic3D() {
		engineInstance.getCurrent3DScene().publicLogic(true);
	}
	
	protected final void logic2D() {
		
	}
	
	protected abstract void runLoop();
	
	protected abstract void renderOneFrame();
	
	//public abstract boolean useDeltaTimeInLogic();
	
}
