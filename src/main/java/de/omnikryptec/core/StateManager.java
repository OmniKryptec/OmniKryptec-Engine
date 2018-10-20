package de.omnikryptec.core;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import de.omnikryptec.old.event.input.InputManager;

public class StateManager {
	
	private static boolean initialized = false;
	
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
			InputManager.closeCallbacks();
			GLFW.glfwTerminate();
			initialized = false;
			System.out.println("Shut down GLFW");
		}
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
