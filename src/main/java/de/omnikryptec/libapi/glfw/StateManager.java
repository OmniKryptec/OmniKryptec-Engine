package de.omnikryptec.libapi.glfw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public final class StateManager {
	
	private static final Collection<Runnable> shutdownHooks = new ArrayList<>();
	private static StateManager instance;
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(()->shutdown(), "Engine-Shutdown-Hooks"));
	}
	
	public static void init() {
		if(isInitialized()) {
			throw new IllegalStateException("GLFW has already been initialized");
		}
		if (GLFW.glfwInit()) {
			GLFWErrorCallback.createThrow().set();
			instance = new StateManager();
			System.out.println("Initialized GLFW");
		} else {
			instance = null;
			throw new RuntimeException("Error while initializing GLFW");
		}
	}

	public static void shutdown() {
		if(isInitialized()) {
			for(Runnable r : shutdownHooks) {
				try {
					r.run();
				}catch(Exception e) {
					System.err.println("Exception in shutdown hook: "+e);
					e.printStackTrace();
				}
			}
			GLFW.glfwTerminate();
			instance = null;
			System.out.println("Shut down GLFW");
		}
	}
	
	public static void registerResourceShutdownHook(Runnable...runnables) {
		shutdownHooks.addAll(Arrays.asList(runnables));
	}
	
	public static boolean isInitialized() {
		return instance!=null;
	}

	public static StateManager active() {
		return instance;
	}
	
	public void pollEvents() {
		GLFW.glfwPollEvents();
	}
	
	public double getTime() {
		return GLFW.glfwGetTime();
	}
	
}
