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

import java.util.ArrayList;
import java.util.HashMap;

import de.omnikryptec.resource.texture.Texture;
import de.omnikryptec.test.saving.DataMap;

public class TexturedModel implements AdvancedModel {

	public static final HashMap<String, ArrayList<TexturedModel>> texturedModels = new HashMap<>();

	private String name;
	private Model model;
	private Material material;

	public TexturedModel() {
	}

	public TexturedModel(String name, Model model, Texture texture) {
		this(name, model, texture, new Material());
	}

	public TexturedModel(String name, Model model, Material material) {
		this(name, model, null, material);
	}

	public TexturedModel(String name, Model model, Texture texture, Material material) {
		this.name = name;
		this.model = model;
		this.material = material;
		this.material.setTexture(Material.DIFFUSE, texture);
		ArrayList<TexturedModel> tms = texturedModels.get(name);
		if (tms == null) {
			tms = new ArrayList<>();
			texturedModels.put(name, tms);
		}
		tms.add(this);
	}

	@Override
	public final Model getModel() {
		return model;
	}

	@Override
	public final Material getMaterial() {
		return material;
	}

	public final TexturedModel setMaterial(Material m) {
		this.material = m;
		return this;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Deletes this AnimatedModel
	 *
	 * @return A reference to this AnimatedModel
	 */
	@Override
	public final TexturedModel delete() {
		model.getVao().delete();
		deleteAll(true);
		return this;
	}

	
	private final TexturedModel deleteAll(boolean all) {
		ArrayList<TexturedModel> tms = (ArrayList<TexturedModel>) texturedModels.get(name).clone();
		if (tms != null) {
			tms.remove(this);
			if (tms.isEmpty()) {
				texturedModels.remove(name);
			} else if (all) {
				tms.stream().forEach((am) -> {
					am.deleteAll(false);
				});
			}
		}
		return this;
	}

	@Override
	public final TexturedModel copy() {
		return new TexturedModel(name, model, material.getTexture(Material.DIFFUSE), material);
	}

	public static final TexturedModel byName(String name) {
		final ArrayList<TexturedModel> tms = texturedModels.get(name);
		if (tms != null && !tms.isEmpty()) {
			return tms.get(0).copy();
		}
		return null;
	}

	@Override
	public DataMap toDataMap(DataMap data) {
		return data;
	}

	@Override
	public TexturedModel fromDataMap(DataMap data) {
		return this;
	}

}
