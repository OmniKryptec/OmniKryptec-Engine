package de.omnikryptec.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class StateManager {
	
	private static boolean initialized = false;
	private static final Collection<Runnable> shutdownHooks = new ArrayList<>();

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(()->shutdown(), "Engine-Shutdown-Hooks"));
	}
	
	public static void init() {
		if(initialized) {
			throw new IllegalStateException("GLFW has already been initialized");
		}
		if (GLFW.glfwInit()) {
			GLFWErrorCallback.createThrow().set();
			initialized = true;
			System.out.println("Initialized GLFW");
		} else {
			initialized = false;
			throw new RuntimeException("Error while initializing GLFW");
		}
	}

	public static void shutdown() {
		if(initialized) {
			for(Runnable r : shutdownHooks) {
				try {
					r.run();
				}catch(Exception e) {
					System.err.println("Exception in shutdown hook: "+e);
					e.printStackTrace();
				}
			}
			GLFW.glfwTerminate();
			initialized = false;
			System.out.println("Shut down GLFW");
		}
	}
	
	public static void registerResourceShutdownHook(Runnable...runnables) {
		shutdownHooks.addAll(Arrays.asList(runnables));
	}
	
	public static boolean isInitialized() {
		return initialized;
	}


	//TODO move? (Instance class or object?)
	/********************************************/
	public static void pollEvents() {
		GLFW.glfwPollEvents();
	}
	
	public static double getTime() {
		return GLFW.glfwGetTime();
	}
	/********************************************/
	
}
