/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.renderer.d3;

import java.util.LinkedList;
import java.util.List;

import de.omnikryptec.old.animation.renderer.AnimatedModelRenderer;
import de.omnikryptec.old.gameobject.terrain.TerrainRenderer;
import de.omnikryptec.old.util.SerializationUtil;
import de.omnikryptec.old.util.exceptions.OmniKryptecException;
import de.omnikryptec.old.util.logger.LogLevel;
import de.omnikryptec.old.util.logger.Logger;

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