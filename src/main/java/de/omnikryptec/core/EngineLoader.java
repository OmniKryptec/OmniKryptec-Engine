package de.omnikryptec.core;

import org.lwjgl.system.Configuration;

import de.omnikryptec.libapi.glfw.GLFWManager;
import de.omnikryptec.util.data.settings.Defaultable;
import de.omnikryptec.util.data.settings.Settings;
import de.omnikryptec.libapi.glfw.OpenGLWindowInfo;
import de.omnikryptec.libapi.glfw.Window;
import de.omnikryptec.libapi.glfw.WindowInfo;

public abstract class EngineLoader {

	public static enum LoaderSetting implements Defaultable {
		DEBUG(false), DEBUG_FUNCTIONS(false), FASTMATH(true), WINDOW_INFO(new OpenGLWindowInfo());

		private final Object defaultSetting;

		private LoaderSetting(Object def) {
			this.defaultSetting = def;
		}

		@Override
		public Object getDefault() {
			return defaultSetting;
		}
	}

	public static void setConfiguration(Settings<LoaderSetting> settings) {
		boolean slowmath = settings.get(LoaderSetting.FASTMATH);
		boolean debug = settings.get(LoaderSetting.DEBUG);
		boolean functionDebug = settings.get(LoaderSetting.DEBUG_FUNCTIONS);
		if (!slowmath) {
			System.setProperty("joml.fastmath", "true");
		}
		Configuration.DEBUG.set(debug);
		Configuration.DEBUG_LOADER.set(debug);
		Configuration.DEBUG_FUNCTIONS.set(debug && functionDebug);
	}

	public static void initialize() {
		// Initialize everything required
		GLFWManager.init();
		// Window, Audio, etc....
	}

	private Window<?> window;

	public EngineLoader() {
	}

	public EngineLoader boot() {
		Settings<LoaderSetting> loaderSettings = new Settings<>();
		config(loaderSettings);
		// load natives?
		setConfiguration(loaderSettings);
		// or load natives here?
		// or let them be loaded by Configuration.SHARED_LIBRARY and LIBRARY_PATH
		initialize();
		window = ((WindowInfo<?>) loaderSettings.get(LoaderSetting.WINDOW_INFO)).createWindow();
		initialized();
		// Start game loop? / Do nothing? / Extra command?
		// Window opening and closing?
		return this;
	}

	public EngineLoader shutdown() {
		onShutdown();
		// Shut down, close window etc... / Do this as shutdown resource hook?!?!?!
		GLFWManager.shutdown();
		return this;
	}

	public Window<?> getWindow() {
		return window;
	}

	protected void config(Settings<LoaderSetting> settings) {
	}

	protected abstract void initialized();

	protected void onShutdown() {
	}
}
