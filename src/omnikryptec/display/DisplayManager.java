package omnikryptec.display;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import omnikryptec.input.InputUtil;

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
	
	private DisplayManager() {
	}


	public static boolean createDisplay(String name, GameSettings settings) {
		return createDisplay(name, settings, new OpenGLInfo());
	}

	public static boolean createDisplay(String name, GameSettings settings, OpenGLInfo info) {
		DisplayManager.settings = settings;
		if (name == null) {
			name = "";
		}
		try {
			if (!resize(settings.getWidth(), settings.getHeight(), settings.wantsFullscreen())) {
				return false;
			}
			if(settings.getInitialFpsCap()!=-1){
				setSyncFPS(settings.getInitialFpsCap());
			}
			Display.setLocation(-1, -1);
			Display.setResizable(settings.wantsResizeable());
			Display.create(info.getPixelFormat(), info.getAttribs());
			Display.setTitle(name);
			if(settings.getMultiSamples()!=GameSettings.NO_MULTISAMPLING){
				GL11.glEnable(GL13.GL_MULTISAMPLE);
			}
			GL11.glViewport(0, 0, settings.getWidth(), settings.getHeight());
//			GL11.glEnable(GL11.GL_BLEND);
//			GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
			lasttime = getCurrentTime();
			return true;
		} catch (Exception e) {
			return false;
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
	public static boolean resize(int width, int height, boolean fullscreen) {
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
	public static void setSyncFPS(int fps) {
		sync = fps;
	}

	/**
	 * updates the display
	 */
	public static void updateDisplay() {
		if (Display.wasResized()) {
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		}
		Display.update();
		if (sync > 0) {
			Display.sync(sync);
		}
		long currentFrameTime = getCurrentTime();
		deltatime = (currentFrameTime - lasttime) / 1000f;
		lasttime = currentFrameTime;
		InputUtil.nextFrame();
	}

	public static float getDeltaTime() {
		return deltatime;
	}

	public static long getFPS() {
		return Math.round(1.0 / deltatime);
	}

	public static long getCurrentTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}

	/**
	 * the fpscap for renderig. if its equal to 0 there is no FPScap
	 * 
	 * @return
	 */
	public static int getFPSCap() {
		return sync;
	}
	
	public static GameSettings getSettings(){
		return settings;
	}

}
