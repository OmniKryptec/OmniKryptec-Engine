/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.old.gameobject.terrain;

import de.omnikryptec.old.resource.model.Model;
import de.omnikryptec.old.resource.objConverter.ModelData;
import de.omnikryptec.old.resource.texture.Texture;

public class TerrainCreator {

    final float worldx, worldz, size;
    final int vertex_count;
    private final TerrainGenerator generator;
    private Model model;
    private ModelData data;
    boolean ismodelcreated = false;
    boolean isgenerated = false;
    final String texmname;
    final TerrainTexturePack texturePack;
    final Texture blendMap;
    final float y;

    public TerrainCreator(String texmname, TerrainTexturePack texturePack, Texture blendMap, final float worldx,
	    final float worldz, final TerrainGenerator generator, final float size, final int vertex_count) {
	this(texmname, texturePack, blendMap, 0, worldx, worldz, generator, size, vertex_count);
    }

    public TerrainCreator(String texmname, TerrainTexturePack texturePack, Texture blendMap, float y,
	    final float worldx, final float worldz, final TerrainGenerator generator, final float size,
	    final int vertex_count) {
	this.worldx = worldx;
	this.worldz = worldz;
	this.generator = generator;
	this.size = size;
	this.vertex_count = vertex_count;
	this.texmname = texmname;
	this.texturePack = texturePack;
	this.blendMap = blendMap;
	this.y = y;
    }

    public TerrainCreator generate() {
	data = Terrain.generateTerrain(worldx, worldz, generator, size, vertex_count);
	isgenerated = true;
	return this;
    }

    public TerrainCreator createModel() {
	if (!isgenerated) {
	    return this;
	}
	model = new Model("terrain_wx_" + worldx + "_wz_" + worldz, data);
	ismodelcreated = true;
	return this;
    }

    public Terrain createTerrain() {
	return new Terrain(this);
    }

    public ModelData getModelData() {
	return data;
    }

    public Model getModel() {
	return model;
    }

}
