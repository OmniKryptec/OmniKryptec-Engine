package de.omnikryptec.core;

import javax.annotation.Nonnull;
import org.lwjgl.system.Configuration;

import de.omnikryptec.libapi.glfw.LibAPIManager;
import de.omnikryptec.util.data.settings.Defaultable;
import de.omnikryptec.util.data.settings.Settings;
import de.omnikryptec.libapi.glfw.OpenGLWindowInfo;
import de.omnikryptec.libapi.glfw.Window;
import de.omnikryptec.libapi.glfw.WindowInfo;

/**
 * The application entry point of the Omnikryptec-Engine. Can be used static and
 * non-static.<br>
 * <ul>
 * <li>In the non-static case, the engine will be started via
 * {@link #boot()}.</li>
 * <li>In the static case, this class only provides utility functions.</li>
 * </ul>
 * The use of this class is not a requirement to use the Engine; librarys and
 * parts of the Engine can be initialized/used independently.
 * 
 * @author pcfreak9000
 *
 */
public abstract class EngineLoader {

    public static enum LoaderSetting implements Defaultable {
	/**
	 * Enables debug mode of the Omnikryptec-Engine and LWJGL. This might do
	 * expensive checks, performance-wise.<br>
	 * <br>
	 * The default value is <code>false</code>.
	 * 
	 * @see org.lwjgl.system.Configuration#DEBUG
	 * @see org.lwjgl.system.Configuration#DEBUG_LOADER
	 */
	DEBUG(false),
	/**
	 * When enabled, lwjgl's capabilities classes will print an error message when
	 * they fail to retrieve a function pointer. <br>
	 * Requires {@link #DEBUG} to be enabled. <br>
	 * <br>
	 * The default value is <code>false</code>.
	 * 
	 * @see org.lwjgl.system.Configuration#DEBUG_FUNCTIONS
	 */
	DEBUG_FUNCTIONS(false),
	/**
	 * Enables joml's fastmath. <br>
	 * <br>
	 * The default value is <code>true</code>.
	 * 
	 * @see org.joml.Math
	 */
	FASTMATH(true),
	/**
	 * The window-/contextcreation information. Only in non-static cases of
	 * {@link EngineLoader}.<br>
	 * <br>
	 * The default value is a default {@link OpenGLWindowInfo}.
	 * 
	 * @see WindowInfo
	 */
	WINDOW_INFO(new OpenGLWindowInfo()),
	/**
	 * When to show the window, after it's creation. Only in non-static cases of
	 * {@link EngineLoader}. <br>
	 * <br>
	 * The default value is {@link WindowMakeVisible#IMMEDIATELY}.
	 * 
	 * @see WindowMakeVisible
	 */
	SHOW_WINDOW_AFTER_CREATION(WindowMakeVisible.IMMEDIATELY);

	private final Object defaultSetting;

	private LoaderSetting(Object def) {
	    this.defaultSetting = def;
	}

	@Override
	public Object getDefault() {
	    return defaultSetting;
	}
    }

    /**
     * Will only be used in non-static cases of {@link EngineLoader}. Defines when
     * to show the {@link Window}.
     * 
     * @author pcfreak9000
     *
     */
    public static enum WindowMakeVisible {
	/**
	 * Show the window immediately after creation, and before
	 * {@link EngineLoader#onContextCreationFinish()}.
	 */
	IMMEDIATELY,
	/**
	 * Show the window after {@link EngineLoader#onContextCreationFinish()}.
	 */
	AFTERINIT,
	/**
	 * Never show the window.
	 */
	NEVER;
    }

    private static boolean debug = (boolean) LoaderSetting.DEBUG.getDefault();

    /**
     * Uses the settings to set library options. This method is only effective if no
     * library functions have been called yet.<br>
     * The library options this method might modify:<br>
     * <ol>
     * <li>{@link LoaderSetting.DEBUG}</li>
     * <li>{@link LoaderSetting.DEBUG_FUNCTIONS}</li>
     * <li>{@link LoaderSetting.FASTMATH}</li>
     * </ol>
     * 
     * @param settings the {@link Settings} to set the lib options from
     */
    public static void setConfiguration(@Nonnull Settings<LoaderSetting> settings) {
	debug = settings.get(LoaderSetting.DEBUG);
	boolean fastmath = settings.get(LoaderSetting.FASTMATH);
	boolean functionDebug = settings.get(LoaderSetting.DEBUG_FUNCTIONS);
	if (fastmath) {
	    System.setProperty("joml.fastmath", "true");
	}
	Configuration.DEBUG.set(debug);
	Configuration.DEBUG_LOADER.set(debug);
	Configuration.DEBUG_FUNCTIONS.set(debug && functionDebug);
    }

    public static void initialize() {
	// Initialize everything required
	LibAPIManager.init();
	// Audio, etc....
    }

    /**
     * The state of the debug-flag, set by {@link #setConfiguration(Settings)}
     * 
     * @return the state of the internal debug-flag.
     * @see LoaderSetting.DEBUG
     */
    public static boolean isDebug() {
	return debug;
    }

    private Window<?> window;
    private boolean booted;

    public EngineLoader() {
    }

    @Nonnull
    public EngineLoader boot() {
	Settings<LoaderSetting> loaderSettings = new Settings<>();
	config(loaderSettings);
	setConfiguration(loaderSettings);
	// or let them (the natives) be loaded by Configuration.SHARED_LIBRARY and
	// LIBRARY_PATH <-- Seems to work, so better use it
	initialize();
	window = ((WindowInfo<?>) loaderSettings.get(LoaderSetting.WINDOW_INFO)).createWindow();
	booted = true;
	if (loaderSettings.get(LoaderSetting.SHOW_WINDOW_AFTER_CREATION) == WindowMakeVisible.IMMEDIATELY) {
	    window.setVisible(true);
	}
	onContextCreationFinish();
	if (loaderSettings.get(LoaderSetting.SHOW_WINDOW_AFTER_CREATION) == WindowMakeVisible.AFTERINIT) {
	    window.setVisible(true);
	}
	// Start game loop? / Do nothing? / Extra command?
	return this;
    }

    public void shutdown() {
	onShutdown();
	// Shutdown, etc...
	window.dispose();
	LibAPIManager.shutdown();
    }

    public Window<?> getWindow() {
	if (!booted) {
	    throw new IllegalStateException("Window is not created yet");
	}
	return window;
    }

    public boolean isBooted() {
	return booted;
    }

    protected void config(Settings<LoaderSetting> settings) {
    }

    protected abstract void onContextCreationFinish();

    protected void onShutdown() {
    }
}
