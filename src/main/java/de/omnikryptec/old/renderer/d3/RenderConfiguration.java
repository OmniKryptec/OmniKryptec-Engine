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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.joml.Vector4f;

import de.omnikryptec.old.util.EnumCollection.RendererTime;

public class RenderConfiguration implements Cloneable{

	public static enum AllowedRenderer {
		All, EvElse, OnlThis;
	}

	private String shaderpackKeyName;
	private boolean renderParticle = true;
	private HashMap<RendererTime, Boolean> allow = new HashMap<>();
	private float maxExpensive = Float.POSITIVE_INFINITY;
	private float minExpensive = Float.NEGATIVE_INFINITY;
	private ArrayList<Renderer> renderer = new ArrayList<>();
	private AllowedRenderer allowedRenderer = AllowedRenderer.All;
	private Vector4f clipplane = new Vector4f(0);
	private int shaderlvl=0;
	
	private LinkedList<Renderer> rendererCache;

	public RenderConfiguration() {
		this(null);
	}
	
	public RenderConfiguration(String sh) {
		this.shaderpackKeyName = sh;
		for(RendererTime t : RendererTime.values()) {
			allow.put(t, true);
		}
		calcCache();
	}

	public RenderConfiguration setRenderParticles(boolean b) {
		this.renderParticle = b;
		return this;
	}

	public RenderConfiguration setExpensiveMinMax(float mine, float maxe) {
		maxExpensive = maxe;
		minExpensive = mine;
		calcCache();
		return this;
	}

	public RenderConfiguration setHighestExpensiveRenderer(float e) {
		maxExpensive = e;
		calcCache();
		return this;
	}

	public RenderConfiguration setSmallestExpensiveRenderer(float e) {
		minExpensive = e;
		calcCache();
		return this;
	}

	public RenderConfiguration setClipPlane(Vector4f cp) {
		this.clipplane = cp;
		return this;
	}
	
	public RenderConfiguration setShaderLvl(int lvl) {
		this.shaderlvl = lvl;
		return this;
	}
	
	public RenderConfiguration setRendererData(AllowedRenderer option, Renderer... renderers) {
		renderer.clear();
		for (Renderer r : renderers) {
			renderer.add(r);
		}
		this.allowedRenderer = option;
		calcCache();
		return this;
	}

	public RenderConfiguration setShaderpackKey(String s) {
		this.shaderpackKeyName = s;
		return this;
	}
	
	public LinkedList<Renderer> getRenderer() {
		return rendererCache;
	}

	public boolean renderParticles() {
		return renderParticle;
	}
	
	public String getShaderpackKey() {
		return shaderpackKeyName;
	}
	
	public boolean isRendererTimeAllowed(RendererTime t) {
		return allow.get(t);
	}
	
	private void calcCache() {
		List<Renderer> all;
		if (allowedRenderer == AllowedRenderer.OnlThis) {
			all = renderer;
		} else if (allowedRenderer == AllowedRenderer.EvElse) {
			List<Renderer> tmpall = RendererRegistration.getAllRenderer();
			all = new ArrayList<>();
			for (Renderer r : tmpall) {
				if (!renderer.contains(r)) {
					all.add(r);
				}
			}
		} else {
			all = RendererRegistration.getAllRenderer();
		}
		LinkedList<Renderer> tmp = new LinkedList<>();
		for (Renderer r : all) {
			if (r.expensiveLevel() <= maxExpensive && r.expensiveLevel() >= minExpensive) {
				tmp.add(r);
			}
		}
		rendererCache = tmp;
	}

	public Vector4f getClipPlane() {
		return clipplane;
	}

	public int getShaderLvl() {
		return shaderlvl;
	}
	
	
	@Override
	public RenderConfiguration clone() {
		RenderConfiguration rofl = new RenderConfiguration(shaderpackKeyName);
		rofl.renderParticle = renderParticle;
		rofl.maxExpensive = maxExpensive;
		rofl.minExpensive = minExpensive;
		rofl.renderer = (ArrayList<Renderer>) renderer.clone();
		rofl.allow = (HashMap<RendererTime, Boolean>) allow.clone();
		rofl.allowedRenderer = allowedRenderer;
		rofl.clipplane = new Vector4f(clipplane);
		rofl.shaderlvl = shaderlvl;
		
		rofl.rendererCache = (LinkedList<Renderer>) rendererCache.clone();
		return rofl;
	}
}
