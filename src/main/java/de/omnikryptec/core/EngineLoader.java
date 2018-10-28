package de.omnikryptec.core;

import org.lwjgl.system.Configuration;

import de.omnikryptec.libapi.glfw.LibAPIManager;
import de.omnikryptec.util.data.settings.Defaultable;
import de.omnikryptec.util.data.settings.Settings;
import de.omnikryptec.libapi.glfw.OpenGLWindowInfo;
import de.omnikryptec.libapi.glfw.Window;
import de.omnikryptec.libapi.glfw.WindowInfo;

public abstract class EngineLoader {

	public static enum LoaderSetting implements Defaultable {
		DEBUG(false), DEBUG_FUNCTIONS(false), FASTMATH(true), WINDOW_INFO(new OpenGLWindowInfo()), SHOW_WINDOW_AFTER_CREATION(true);

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
		boolean fastmath = settings.get(LoaderSetting.FASTMATH);
		boolean debug = settings.get(LoaderSetting.DEBUG);
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
		if((boolean) loaderSettings.get(LoaderSetting.SHOW_WINDOW_AFTER_CREATION)) {
			window.show();
		}
		initialized();
		// Start game loop? / Do nothing? / Extra command?
		// Window opening and closing?
		return this;
	}

	public EngineLoader shutdown() {
		onShutdown();
		// Shut down, close window etc...
		LibAPIManager.shutdown();
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