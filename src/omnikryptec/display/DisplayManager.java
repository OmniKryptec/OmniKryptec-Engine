package omnikryptec.display;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import omnikryptec.audio.AudioManager;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.settings.GameSettings;
import omnikryptec.util.GraphicsUtil;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;
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

	private static boolean smootheddelteenabled = true;
	private static double[] smootheddelta = new double[250];
	private static int deltapointer = 0;
	
	private static long framecount = 0;

	/**
	 * for performance
	 */
	private static float runtimef = 0;
	private static float deltatimef = 0;

	private double serverOffset=0;
	
	public static final int DISABLE_FPS_CAP = 0;

	private GameSettings settings;

	private DisplayManager(GameSettings settings) {
		this.settings = settings;
		lasttime = getCurrentTime();
		setSyncFPS(settings.getInteger(GameSettings.FPS_CAP));
		Profiler.addProfilable(this, 5);
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
		return createDisplay(name, settings, new GLFWInfo());
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
	public static final OmniKryptecEngine createDisplay(String name, GameSettings settings, GLFWInfo info) {
		if (OmniKryptecEngine.isCreated()) {
			throw new IllegalStateException("The Engine is already created!");
		}
		if(settings.getBoolean(GameSettings.FASTMATH)) {
			System.setProperty("joml.fastmath", "true");
		}
		if (name == null) {
			name = "";
		}
		try {
			Display.create(name, info);
			AudioManager.init();
			if (settings.getMultiSamples() != GameSettings.NO_MULTISAMPLING) {
				GraphicsUtil.antialias(true);
			}
			GraphicsUtil.cullBackFaces(true);
			GraphicsUtil.enableDepthTesting(true);
			GL11.glViewport(0, 0, info.getWidth(), info.getHeight());
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			Logger.log("Successfully created the Display!", LogLevel.FINEST);
			return new OmniKryptecEngine(new DisplayManager(settings));
		} catch (Exception ex) {
			Logger.logErr("Error while creating new DisplayManager: " + ex, ex);
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
	
	private double currentFrameTime=0;
	private double tmptime=0, tmptime2;
	private double updateTime=0;
	private double idletime=0;
	private double fpstime=0;
	
	private long fps1=0, fps2=0;
	private boolean fps=true;
	
	private boolean isfirst=true;
	/**
	 * Updates the display
	 * 
	 * @return DisplayManager A reference to this DisplayManager
	 */
	public final DisplayManager updateDisplay() {
		if(isfirst){
			lasttime = getCurrentTime();
			fpstime = getCurrentTime()/1000.0;
			isfirst = false;
		}
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
		if(fps) {
			fps1++;
		}else {
			fps2++;
		}
		tmptime2 = getCurrentTime();
		if(runtime-fpstime>=1.0) {
			fpstime = runtime;
			if(fps) {
				fps = false;
				fps2 = 0;
			}else {
				fps = true;
				fps1 = 0;
			}
		}
		if (sync > DISABLE_FPS_CAP) {
			Display.sync(sync);
		}
		idletime = getCurrentTime() - tmptime2;
		updateTime = getCurrentTime() - tmptime - idletime;
		return this;
	}

	public final double getUpdateTimeMS(){
		return updateTime;
	}
	
	public final double getIdleTimeMS(){
		return idletime;
	}
	
	/**
	 * Returns the time since the last frame update
	 * 
	 * @return Float Time in seconds
	 */
	public final float getDUDeltaTimef() {
		return deltatimef;
	}
	
	public final double getDUDeltaTime(){
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
	public final double getFPS() {
		return 1.0 / deltatime;
	}

	public final long getFPSCounted() {
		return fps ? fps2 : fps1;
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
	 * @return double Time in milliseconds
	 */
	public final double getCurrentTime() {
		return Display.getCurrentTime();
	}

	
	public final double getServerCurrentTime(){
		return getCurrentTime()+serverOffset;
	}
	
	public final DisplayManager setServerTimeOffset(double d){
		this.serverOffset = d;
		return this;
	}
	
	public final double getServerTimeOffset(){
		return serverOffset;
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
