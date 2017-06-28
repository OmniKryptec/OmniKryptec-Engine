package omnikryptec.display;

import java.awt.DisplayMode;

import org.lwjgl.opengl.GL11;

import omnikryptec.audio.AudioManager;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.settings.GameSettings;
import omnikryptec.util.RenderUtil;
import omnikryptec.util.profiler.Profilable;
import omnikryptec.util.profiler.ProfileContainer;
import omnikryptec.util.profiler.Profiler;

/**
 * Display managing class
 * 
 * @author pcfreak9000 &amp; Panzer1119
 */
public class DisplayManager implements Profilable{

	private static int sync = 240;

	private static double deltatime = 0;
	private static double lasttime = 0;
	private static double runtime = 0;

	private static boolean smootheddelteenabled = false;
	private static double[] smootheddelta = new double[60];
	private static int deltapointer = 0;
	
	private static long framecount = 0;

	/**
	 * for performance
	 */
	private static float runtimef = 0;
	private static float deltatimef = 0;

	
	
	public static final int DISABLE_FPS_CAP = 0;

	private static GameSettings settings;

	private static DisplayManager manager;

	private DisplayManager() {
		manager = this;
		Profiler.addProfilable(this, 5);
	}

	/**
	 * Returns the DisplayManager instance
	 * 
	 * @return DisplayManager DisplayManager
	 */
	public static final DisplayManager instance() {
		return manager;
	}
	
	/**
	 * Creates a OmniKryptecEngine and a DisplayManager
	 * 
	 * @param name
	 *            String Name
	 * @param settings
	 *            GameSettings Game settings
	 * @return OmniKryptecEngine OmniKryptecEngine
	 */
	public static final OmniKryptecEngine createDisplay(String name, GameSettings settings) {
		return createDisplay(name, settings, new OpenGLInfo());
	}

	/**
	 * Creates a OmniKryptecEngine and a DisplayManager
	 * 
	 * @param name
	 *            String Name
	 * @param settings
	 *            GameSettings Game settings
	 * @param info
	 *            OpenGLInfo Info
	 * @return OmniKryptecEngine OmniKryptecEngine
	 */
	public static final OmniKryptecEngine createDisplay(String name, GameSettings settings, OpenGLInfo info) {
		if (manager != null) {
			throw new IllegalStateException("The DisplayManager is already created!");
		}
		DisplayManager.settings = settings;
		new DisplayManager();
		if (name == null) {
			name = "";
		}
		try {
			if (!manager.resize(settings.getWidth(), settings.getHeight(), settings.wantsFullscreen())) {
				return null;
			}
			manager.setSyncFPS(settings.getInitialFPSCap());
			Display.setLocation(-1, -1);
			Display.setResizable(settings.wantsResizable());
			Display.create(info.getPixelFormat(), info.getAttribs());
			Display.setTitle(name);
			if (settings.getMultiSamples() != GameSettings.NO_MULTISAMPLING) {
				RenderUtil.antialias(true);
			}
			GL11.glViewport(0, 0, settings.getWidth(), settings.getHeight());
			AudioManager.init();
			lasttime = manager.getCurrentTime();
			return new OmniKryptecEngine(manager);
		} catch (Exception ex) {
			if (Logger.isDebugMode()) {
				Logger.logErr("Error while creating new DisplayManager: " + ex, ex);
			}
			return null;
		}
	}

	/**
	 * Resizes the display
	 * 
	 * @param width
	 *            Integer Width
	 * @param height
	 *            Integer Height
	 * @param fullscreen
	 *            Boolean Tries to make a fullscreen display
	 * @return <tt>false</tt> if the resizing failed
	 */
	public final boolean resize(int width, int height, boolean fullscreen) {
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
	 * Sets the FPS cap Default is 240, DISABLE_FPS_CAP disables the cap
	 * 
	 * @param fps
	 *            Integer FPS
	 * @return DisplayManager A reference to this DisplayManager
	 */
	public final DisplayManager setSyncFPS(int fps) {
		sync = fps;
		return this;
	}
	
	private long currentFrameTime=0;
	private long tmptime=0, tmptime2;
	private long updateTime=0;
	private long idletime=0;
	
	
	/**
	 * Updates the display
	 * 
	 * @return DisplayManager A reference to this DisplayManager
	 */
	public final DisplayManager updateDisplay() {
		tmptime = getCurrentTime();
		if (Display.wasResized()) {
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		}
		currentFrameTime = getCurrentTime();
		deltatime = (currentFrameTime - lasttime) / 1000.0;
		runtime += deltatime;
		lasttime = currentFrameTime;
		deltatimef = (float) deltatime;
		if (smootheddelteenabled) {
			smootheddelta[deltapointer] = deltatime;
			deltapointer++;
			deltapointer %= smootheddelta.length;
		}
		runtimef = (float) runtime;
		framecount++;
		Display.update();
		tmptime2 = getCurrentTime();
		if (sync > DISABLE_FPS_CAP) {
			Display.sync(sync);
		}
		idletime = getCurrentTime() - tmptime2;
		updateTime = getCurrentTime() - tmptime - idletime;
		return this;
	}

	public final long getUpdateTimeMS(){
		return updateTime;
	}
	
	public final long getIdleTimeMS(){
		return idletime;
	}
	
	/**
	 * Returns the time since the last frame update
	 * 
	 * @return Float Time in seconds
	 */
	public final float getDeltaTimef() {
		return deltatimef;
	}
	
	public final double getDeltaTime(){
		return deltatime;
	}

	/**
	 * Returns the time since the first update
	 * 
	 * @return Float Time in seconds
	 */
	public final float getRunTime() {
		return runtimef;
	}

	/**
	 * Returns the FPS
	 * 
	 * @return Long FPS
	 */
	public final long getFPS() {
		return Math.round(1.0 / deltatime);
	}

	/**
	 * Returns the number of update calls
	 * 
	 * @return
	 */
	public final long getFramecount() {
		return framecount;
	}

	/**
	 * Returns the actual time
	 * 
	 * @return Float Time in milliseconds
	 */
	public final long getCurrentTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}

	
	/**
	 * Returns the FPS cap, if it is DISABLE_FPS_CAP, the cap is disabled
	 * 
	 * @return Integer FPS cap
	 */
	public final int getFPSCap() {
		return sync;
	}

	/**
	 * Returns the used GameSettings
	 * 
	 * @return GameSettings Game settings
	 */
	public final GameSettings getSettings() {
		return settings;
	}

	/**
	 * Closes the display
	 * 
	 * @return DisplayManager A reference to this DisplayManager
	 */
	public final DisplayManager close() {
		AudioManager.cleanup();
		Display.destroy();
		return this;
	}
	
	public final DisplayManager setSmoothedDeltatime(boolean b){
		smootheddelteenabled = b;
		return this;
	}
	
	public final DisplayManager setSmoothedFrames(int i){
		smootheddelta = new double[i];
		deltapointer = 0;
		return this;
	}
	
	public final double getSmoothedDeltaTime() {
		if (!smootheddelteenabled) {
			Logger.log("Smoothed deltatime is not enabled!", LogLevel.WARNING);
		}
		double del = 0;
		for (int i = 0; i < smootheddelta.length; i++) {
			del += smootheddelta[i];
		}
		return del / smootheddelta.length;
	}

	public final long getSmoothedFPS() {
		return Math.round(1.0 / (getSmoothedDeltaTime()));
	}

	@Override
	public ProfileContainer[] getProfiles() {
		return new ProfileContainer[]{new ProfileContainer(Profiler.DISPLAY_UPDATE_TIME, getUpdateTimeMS()), new ProfileContainer(Profiler.DISPLAY_IDLE_TIME, getIdleTimeMS())};
	}
}
