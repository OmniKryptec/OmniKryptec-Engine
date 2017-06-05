package omnikryptec.display;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import omnikryptec.audio.AudioManager;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.settings.GameSettings;
import omnikryptec.util.RenderUtil;

/**
 * Display managing class
 * @author pcfreak9000
 */
public class DisplayManager {

    private static int sync = 240;

    private static double deltatime = 0;
    private static double lasttime = 0;

    public static final int DISABLE_FPS_CAP = 0;

    private static GameSettings settings;

    private static DisplayManager manager;

    private DisplayManager() {
        
    }

    /**
     * Returns the DisplayManager instance
     * @return DisplayManager DisplayManager
     */
    public static final DisplayManager instance(){
        return manager;
    }

    /**
     * Creates a OmniKryptecEngine and a DisplayManager
     * @param name String Name
     * @param settings GameSettings Game settings
     * @return OmniKryptecEngine OmniKryptecEngine
     */
    public static final OmniKryptecEngine createDisplay(String name, GameSettings settings) {
        return createDisplay(name, settings, new OpenGLInfo());
    }

    /**
     * Creates a OmniKryptecEngine and a DisplayManager
     * @param name String Name
     * @param settings GameSettings Game settings
     * @param info OpenGLInfo Info
     * @return OmniKryptecEngine OmniKryptecEngine
     */
    public static final OmniKryptecEngine createDisplay(String name, GameSettings settings, OpenGLInfo info) {
        if(manager != null){
            throw new IllegalStateException("The DisplayManager is already created!");
        }
        DisplayManager.settings = settings;
        manager = new DisplayManager();
        if(name == null) {
            name = "";
        }
        try {
            if(!manager.resize(settings.getWidth(), settings.getHeight(), settings.wantsFullscreen())) {
                return null;
            }
            if(settings.getInitialFpsCap() != -1){
                manager.setSyncFPS(settings.getInitialFpsCap());
            }
            Display.setLocation(-1, -1);
            Display.setResizable(settings.wantsResizeable());
            Display.create(info.getPixelFormat(), info.getAttribs());
            Display.setTitle(name);
            if(settings.getMultiSamples() != GameSettings.NO_MULTISAMPLING) {
                RenderUtil.antialias(true);
            }
            GL11.glViewport(0, 0, settings.getWidth(), settings.getHeight());
            AudioManager.init();
            lasttime = manager.getCurrentTime();
            return new OmniKryptecEngine(manager);
        } catch (Exception ex) {
            if(Logger.isDebugMode()) {
                Logger.logErr("Error while creating new DisplayManager: " + ex, ex);
            }
            return null;
        }
    }

    /**
     * Resizes the display
     * @param width Integer Width
     * @param height Integer Height
     * @param fullscreen Boolean Tries to make a fullscreen display
     * @return <tt>false</tt> if the resizing failed
     */
    public final boolean resize(int width, int height, boolean fullscreen) {
        try {
            boolean found = false;
            DisplayMode displayMode = null;
            if(fullscreen) {
                DisplayMode[] modes = Display.getAvailableDisplayModes();
                for(int i = 0; i < modes.length; i++) {
                    if(modes[i].getWidth() == width && modes[i].getHeight() == height && modes[i].isFullscreenCapable()) {
                        displayMode = modes[i];
                        found = true;
                    }
                }
                if(found) {
                    Display.setFullscreen(fullscreen);
                } else {
                    Display.setFullscreen(false);
                    displayMode = new DisplayMode(width, height);
                }
            } else {
                displayMode = new DisplayMode(width, height);
            }
            Display.setDisplayMode(displayMode);
            if(Display.isCreated()) {
                GL11.glViewport(0, 0, width, height);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Sets the FPS cap
     * Default is 240, DISABLE_FPS_CAP disables the cap
     * @param fps Integer FPS
     * @return DisplayManager A reference to this DisplayManager
     */
    public final DisplayManager setSyncFPS(int fps) {
        sync = fps;
        return this;
    }

    /**
     * Updates the display
     * @return DisplayManager A reference to this DisplayManager
     */
    public final DisplayManager updateDisplay() {
        if(Display.wasResized()) {
            GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
        }
        long currentFrameTime = getCurrentTime();
        deltatime = (currentFrameTime - lasttime) / 1000.0;
        lasttime = currentFrameTime;
        Display.update();
        if(sync > DISABLE_FPS_CAP) {
            Display.sync(sync);
        }
        return this;
    }

    /**
     * Returns the time since the last frame update
     * @return Float Time in seconds
     */
    public final float getDeltaTime() {
        return (float) deltatime;
    }

    /**
     * Returns the FPS
     * @return Long FPS
     */
    public final long getFPS() {
        return Math.round(1.0 / deltatime);
    }

    /**
     * Returns the actual time
     * @return Float Time in milliseconds
     */
    public final long getCurrentTime() {
        return Sys.getTime() * 1000 / Sys.getTimerResolution();
    }
    
    /**
     * Returns the FPS cap, if it is DISABLE_FPS_CAP, the cap is disabled
     * @return Integer FPS cap
     */
    public final int getFPSCap() {
        return sync;
    }

    /**
     * Returns the used GameSettings
     * @return GameSettings Game settings
     */
    public final GameSettings getSettings(){
        return settings;
    }

    /**
     * Closes the display
     * @return DisplayManager A reference to this DisplayManager
     */
    public final DisplayManager close() {
        AudioManager.cleanup();
        Display.destroy();
        return this;
    }

}
