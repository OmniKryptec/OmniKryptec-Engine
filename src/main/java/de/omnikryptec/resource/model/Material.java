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

package de.omnikryptec.resource.model;

import de.omnikryptec.renderer.d3.Renderer;
import de.omnikryptec.renderer.d3.RendererRegistration;
import de.omnikryptec.resource.texture.Texture;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class Material {
	
	/**
	 * Texture
	 */
	public static final String DIFFUSE = "diffuse";
	/**
	 * Texture
	 */
	public static final String SPECULAR = "specular";
	/**
	 * Texture
	 */
	public static final String NORMAL = "normal";
	/**
	 * Texture, Vec3
	 */
	public static final String SHADERINFO = "info";

	/**
	 * Vec3
	 */
	public static final String REFLECTIVITY = "reflectivity";
	/**
	 * Float
	 */
	public static final String DAMPER = "damper";
	
	private Map<String, Texture> textures = new HashMap<>(); 
	private Map<String, Float> floats = new HashMap<>(); 
	private Map<String, Vector3f> vec3fs = new HashMap<>(); 

	private Renderer renderer = RendererRegistration.SIMPLE_MESH_RENDERER;
	private boolean hasTransparency = false;

	
	public Material setTexture(String type, Texture t){
		textures.put(type, t);
		return this;
	}
	
	public Material setFloat(String type, float f){
		floats.put(type, f);
		return this;
	}
	
	public Material setVector3f(String type, Vector3f v){
		vec3fs.put(type, v);
		return this;
	}
	
	public Texture getTexture(String type){
		return textures.get(type);
	}
	
	public float getFloat(String type){
		return floats.get(type);
	}
	
	public Vector3f getVector3f(String type){
		return vec3fs.get(type);
	}
	
	public final boolean hasTransparency() {
		return hasTransparency;
	}

	public final Material setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
		return this;
	}

	public final Renderer getRenderer() {
		return renderer;
	}
	
	public final Material setRenderer(Renderer renderer) {
		RendererRegistration.exceptionIfNotRegistered(renderer);
		this.renderer = renderer;
		return this;
	}


}
