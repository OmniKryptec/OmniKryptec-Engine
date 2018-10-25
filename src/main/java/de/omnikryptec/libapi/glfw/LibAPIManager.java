package de.omnikryptec.libapi.glfw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public final class LibAPIManager {

	private static final Collection<Runnable> shutdownHooks = new ArrayList<>();
	private static LibAPIManager instance;

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown(), "Engine-Shutdown-Hooks"));
	}

	public static void init() {
		if (isInitialized()) {
			throw new IllegalStateException("GLFW has already been initialized");
		}
		if (GLFW.glfwInit()) {
			GLFWErrorCallback.createThrow().set();
			instance = new LibAPIManager();
			System.out.println("Initialized GLFW");
		} else {
			instance = null;
			throw new RuntimeException("Error while initializing GLFW");
		}
	}

	public static void shutdown() {
		if (isInitialized()) {
			for (Runnable r : shutdownHooks) {
				try {
					r.run();
				} catch (Exception e) {
					System.err.println("Exception in shutdown hook '" + r + "': " + e);
					e.printStackTrace();
				}
			}
			GLFW.glfwTerminate();
			instance = null;
			System.out.println("Shut down GLFW");
		}
	}

	public static void registerResourceShutdownHooks(Runnable... runnables) {
		shutdownHooks.addAll(Arrays.asList(runnables));
	}

	public static boolean isInitialized() {
		return instance != null;
	}

	public static LibAPIManager active() {
		return instance;
	}

	private LibAPIManager() {
	}

	public void pollEvents() {
		GLFW.glfwPollEvents();
	}

	/**
	 * Returns the value of the GLFW timer. The timer measures time elapsed since GLFW was initialized.
	 * 
	 * The resolution of the timer is system dependent, but is usually on the order
	 * of a few micro- or nanoseconds. It uses the highest-resolution monotonictime
	 * source on each supported platform.
	 * 
	 * @return the current value, in seconds, or zero if an error occurred
	 */
	public double getTime() {
		return GLFW.glfwGetTime();
	}

}
