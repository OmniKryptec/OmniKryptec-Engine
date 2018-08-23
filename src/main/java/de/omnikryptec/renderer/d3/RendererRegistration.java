package de.omnikryptec.renderer.d3;

import de.omnikryptec.animation.renderer.AnimatedModelRenderer;
import de.omnikryptec.gameobject.terrain.TerrainRenderer;
import de.omnikryptec.util.SerializationUtil;
import de.omnikryptec.util.exceptions.OmniKryptecException;
import de.omnikryptec.util.logger.LogLevel;
import de.omnikryptec.util.logger.Logger;

import java.util.LinkedList;
import java.util.List;

public class RendererRegistration {

	private static final List<Renderer> existingRenderers = new LinkedList<>();

	public static final SimpleMeshRenderer SIMPLE_MESH_RENDERER;
	public static final ForwardMeshRenderer FORWARD_MESH_RENDERER;

	public static final AnimatedModelRenderer DEF_ANIMATEDMODEL_RENDERER;
	public static final TerrainRenderer DEF_TERRAIN_RENDERER;

	static {
		DEF_ANIMATEDMODEL_RENDERER = new AnimatedModelRenderer();
		FORWARD_MESH_RENDERER = new ForwardMeshRenderer();
		DEF_TERRAIN_RENDERER = new TerrainRenderer();
		SIMPLE_MESH_RENDERER = new SimpleMeshRenderer();
	}

	public static void exceptionIfNotRegistered(Renderer r) {
		if (!exists(r)) {
			Logger.logErr("This renderer is not registered!",
					new OmniKryptecException("Renderer is not registered: " + r.getClass() + " (" + r + ")"));
		}
	}

	public static boolean exists(Renderer r) {
		return existingRenderers.contains(r);
	}

	public static void register(Renderer r) {
		existingRenderers.add(r);
	}

	public static Renderer byName(String name) {
		return byClass(SerializationUtil.classForName(name));
	}

	public static Renderer byClass(Class<?> c) {
		for (Renderer renderer : existingRenderers) {
			if (c == renderer.getClass()) {
				return renderer;
			}
		}
		return null;
	}

	public static List<Renderer> getAllRenderer() {
		return existingRenderers;
	}

	/**
	 * trigger static constructor
	 */
	public static void init() {
		Logger.log("Initializing default renderer", LogLevel.FINE);
	}
}
