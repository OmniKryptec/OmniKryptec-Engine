package omnikryptec.display;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.util.RenderUtil;

/**
 * 
 * @author pcfreak9000
 *
 */
public class DisplayManager {

	private static int sync = 240;

	private static float deltatime = 0;
	private static float lasttime = 0;

	public static final int DISABLE_FPS_CAP = 0;

	private static GameSettings settings;
	
	private static DisplayManager manager;
	
	private DisplayManager() {
	}


	public static DisplayManager instance(){
		return manager;
	}
	
	public static OmniKryptecEngine createDisplay(String name, GameSettings settings) {
		return createDisplay(name, settings, new OpenGLInfo());
	}

	public static OmniKryptecEngine createDisplay(String name, GameSettings settings, OpenGLInfo info) {
		if(manager!=null){
			throw new IllegalStateException("The DisplayManager is already created!");
		}
		DisplayManager.settings = settings;
		manager = new DisplayManager();
		if (name == null) {
			name = "";
		}
		try {
			if (!manager.resize(settings.getWidth(), settings.getHeight(), settings.wantsFullscreen())) {
				return null;
			}
			if(settings.getInitialFpsCap()!=-1){
				manager.setSyncFPS(settings.getInitialFpsCap());
			}
			Display.setLocation(-1, -1);
			Display.setResizable(settings.wantsResizeable());
			Display.create(info.getPixelFormat(), info.getAttribs());
			Display.setTitle(name);
			if(settings.getMultiSamples()!=GameSettings.NO_MULTISAMPLING){
				RenderUtil.antialias(true);
			}
			GL11.glViewport(0, 0, settings.getWidth(), settings.getHeight());
			lasttime = manager.getCurrentTime();
			return new OmniKryptecEngine(manager);
		} catch (Exception e) {
			if(Logger.isDebugMode()){
				e.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * resizes the display
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param fullscreen
	 *            try to make the game fullscreen=
	 * @return false if rezising failed
	 */
	public boolean resize(int width, int height, boolean fullscreen) {
		try {
			boolean found = false;

			DisplayMode displayMode = null;
			if (fullscreen) {

				DisplayMode[] modes = Display.getAvailableDisplayModes();

				for (int i = 0; i < modes.length; i++) {
					if (modes[i].getWidth() == width && modes[i].getHeight() == height
							&& modes[i].isFullscreenCapable()) {
						displayMode = modes[i];
						found = true;
					}
				}
				if (found) {
					Display.setFullscreen(fullscreen);
				} else {
					Display.setFullscreen(false);
					displayMode = new DisplayMode(width, height);
				}
			} else {
				displayMode = new DisplayMode(width, height);
			}
			Display.setDisplayMode(displayMode);
			if (Display.isCreated()) {
				GL11.glViewport(0, 0, width, height);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * sets the FPS-cap; default is 240. set to DISABLE_FPS_CAP to disable the
	 * cap
	 * 
	 * @param fps
	 */
	public void setSyncFPS(int fps) {
		sync = fps;
	}

	/**
	 * updates the display
	 */
	public void updateDisplay() {
		if (Display.wasResized()) {
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		}
		long currentFrameTime = getCurrentTime();
		deltatime = (currentFrameTime - lasttime) / 1000f;
		lasttime = currentFrameTime;
		Display.update();
		if (sync > 0) {
			Display.sync(sync);
		}
	}

	public float getDeltaTime() {
		return deltatime;
	}

	public long getFPS() {
		return Math.round(1.0 / deltatime);
	}

	public long getCurrentTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}

	/**
	 * the fpscap for renderig. if its equal to 0 there is no FPScap
	 * 
	 * @return
	 */
	public int getFPSCap() {
		return sync;
	}
	
	public GameSettings getSettings(){
		return settings;
	}


	public void close() {
		Display.destroy();
	}

}
