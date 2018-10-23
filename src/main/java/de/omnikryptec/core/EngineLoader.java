package de.omnikryptec.core;

import de.omnikryptec.libapi.glfw.GLFWManager;

public abstract class EngineLoader {

	public static void setConfiguration() {
		// Set settings like fastmath and debug stuff
//				if (fastmath) {
//					System.setProperty("joml.fastmath", "true");
//				}
//		if (debug) {
//		}
	}

	public static void initialize() {
		// Initialize everything required
		GLFWManager.init();
		// Window, Audio, etc....
	}

	public EngineLoader() {
	}

	public EngineLoader boot() {
		// Create settings?
		config();
		// load natives?
		setConfiguration();
		// or load natives here?
		// or let them be loaded by Configuration.SHARED_LIBRARY and LIBRARY_PATH
		initialize();
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

	protected abstract void config();

	protected abstract void initialized();

	protected void onShutdown() {
	}
}
