package omnikryptec.main;

import java.util.Vector;

import omnikryptec.display.Display;
import omnikryptec.util.EnumCollection.GameLoopShutdown;

public abstract class GameLoop {
	
	
	private Vector<Runnable> gltasks = new Vector<>();
	private GameLoopShutdown stopLevel = GameLoopShutdown.NOT_NOW;
	
	protected final OmniKryptecEngine engineInstance;

	
	protected GameLoop() {
		engineInstance = OmniKryptecEngine.instance();
	}
	
	
	//TODO check for opengl thread
	public void doGLTasks(int max) {
		while(gltasks.size()>0&&(max>0||max==-1)) {
			gltasks.firstElement().run();
			gltasks.removeElementAt(0);
			max--;
		}
	}
	
	public final void run() {
		stopLevel = GameLoopShutdown.NOT_NOW;
		try {
			runLoop();
		}catch(Exception e) {
			engineInstance.errorOccured(e, "An error occured in the game loop");
		}
		if(Display.isCloseRequested()) {
			engineInstance.requestShutdown();
		}
	}
	
	public final void step() {
		
		try {
			renderOneFrame();
		}catch(Exception e) {
			engineInstance.errorOccured(e, "An error occured in a step of the game loop");
		}
	}
	
	public final void requestStop(GameLoopShutdown option) {
		stopLevel = option;
	}
	
	protected final boolean isStopRequested() {
		return stopLevel.getLvl()>0||Display.isCloseRequested();
	}
	
	protected abstract void runLoop();
	
	protected abstract void renderOneFrame();
	
}
