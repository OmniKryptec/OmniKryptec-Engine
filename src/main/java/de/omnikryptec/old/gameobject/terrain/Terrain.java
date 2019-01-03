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

import de.omnikryptec.old.gameobject.Entity;
import de.omnikryptec.old.renderer.d3.RendererRegistration;
import de.omnikryptec.old.resource.model.Material;
import de.omnikryptec.old.resource.model.Model;
import de.omnikryptec.old.resource.model.TexturedModel;
import de.omnikryptec.old.resource.objConverter.ModelData;
import de.omnikryptec.old.resource.objConverter.Vertex;
import de.omnikryptec.old.resource.texture.Texture;
import de.omnikryptec.old.util.EnumCollection.Dimension;
import de.omnikryptec.old.util.Maths;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;

/**
 * Terrain
 *
 * @author Panzer1119
 */
public class Terrain extends Entity {

    private final TerrainTexturePack texturePack;

    private float[][] heights;

    private float size;

    public Terrain(final float size, final float worldX, final float worldZ, String texturedModelName, ModelData model,
	    TerrainTexturePack texturePack, Texture blendMap) {
	this(size, worldX, worldZ, texturedModelName,
		new Model("Terrain_worldX_" + worldX + "_worldZ_" + worldZ, model), texturePack, blendMap);
    }

    public Terrain(TerrainCreator tc) {
	this(tc.size, tc.worldx, tc.worldz, tc.texmname, dealwithtc(tc), tc.texturePack, tc.blendMap);
    }

    private static Model dealwithtc(TerrainCreator tc) {
	if (!tc.isgenerated) {
	    tc.generate();
	}
	if (!tc.ismodelcreated) {
	    tc.createModel();
	}
	return tc.getModel();
    }

    public Terrain(final float size, final float worldX, final float worldZ, String texturedModelName, Model model,
	    TerrainTexturePack texturePack, Texture blendMap) {
	getTransform().setX(worldX);
	getTransform().setZ(worldZ);
	this.texturePack = texturePack;
	setAdvancedModel(new TexturedModel(texturedModelName, model, blendMap));
	getAdvancedModel().getMaterial().setRenderer(RendererRegistration.DEF_TERRAIN_RENDERER);
	this.size = size;
	createHeights(model.getModelData());
    }

    private void createHeights(ModelData model) {
	if (model == null) {
	    return;
	}
	int vs = (int) Math.sqrt(model.getVertexCount());
	heights = new float[vs][vs];
	for (int i = 0; i < vs; i++) {
	    for (int j = 0; j < vs; j++) {
		heights[j][i] = model.getVertices()[(i * vs + j) * 3 + 1];
	    }
	}
    }

    public float getHeightOfTerrain(float wx, float wz) {
	return getHeightOfTerrain(wx, wz, size);
    }

    // TOD- to be tested!!
    public float getHeightOfTerrain(float worldX, float worldZ, float size) {
	if (heights == null || heights.length == 0) {
	    return getTransform().getPosition(true).y;
	}
	float terrainX = worldX - getTransform().getPosition(true).x;
	float terrainZ = worldZ - getTransform().getPosition(true).z;
	float gridSquareSize = size / ((float) heights.length - 1);
	int gridX = (int) Math.floor(terrainX / gridSquareSize);
	int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
	if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {
	    return 0;
	}
	float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
	float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
	float answer;
	if (xCoord <= (1 - zCoord)) {
	    answer = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0),
		    new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(0, heights[gridX][gridZ + 1], 1),
		    new Vector2f(xCoord, zCoord));
	} else {
	    answer = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0),
		    new Vector3f(1, heights[gridX + 1][gridZ + 1], 1), new Vector3f(0, heights[gridX][gridZ + 1], 1),
		    new Vector2f(xCoord, zCoord));
	}
	return answer + getTransform().getPosition(true).y;
    }

    public static final ModelData generateTerrain(final float worldx, final float worldz,
	    final TerrainGenerator generator, final float size, final int vertex_count) {
	final ArrayList<Vertex> vertices = new ArrayList<>();
	final ArrayList<Vector2f> textures = new ArrayList<>();
	final ArrayList<Vector3f> normals = new ArrayList<>();
	final ArrayList<Integer> indices = new ArrayList<>();
	Vector3f normal;
	Vector2f texture;
	for (int i = 0; i < vertex_count; i++) {
	    for (int j = 0; j < vertex_count; j++) {
		final Vertex vertex = new Vertex(vertices.size(),
			new Vector3f(j / ((float) vertex_count - 1) * size,
				generator.getHeight(worldx + j, worldz + i),
				i / ((float) vertex_count - 1) * size));
		vertices.add(vertex);
		normal = generator.generateNormal(worldx + j, worldz + i);
		vertex.setNormalIndex(normals.size());
		normals.add(normal);
		texture = new Vector2f(j / ((float) vertex_count - 1),
			i / ((float) vertex_count - 1));
		vertex.setTextureIndex(textures.size());
		textures.add(texture);
	    }
	}
	for (int gz = 0; gz < (vertex_count - 1); gz++) {
	    for (int gx = 0; gx < (vertex_count - 1); gx++) {
		int topLeft = (gz * vertex_count) + gx;
		int topRight = topLeft + 1;
		int bottomLeft = ((gz + 1) * vertex_count) + gx;
		int bottomRight = bottomLeft + 1;
		Vertex v_1_1 = vertices.get(topLeft);
		Vertex v_1_2 = vertices.get(bottomLeft);
		Vertex v_1_3 = vertices.get(topRight);
		v_1_1 = processVertex(v_1_1, v_1_1.getTextureIndex(), v_1_1.getNormalIndex(), indices, vertices);
		v_1_2 = processVertex(v_1_2, v_1_2.getTextureIndex(), v_1_2.getNormalIndex(), indices, vertices);
		v_1_3 = processVertex(v_1_3, v_1_3.getTextureIndex(), v_1_3.getNormalIndex(), indices, vertices);
		calculateTangents(v_1_1, v_1_2, v_1_3, textures);
		Vertex v_2_1 = vertices.get(topRight);
		Vertex v_2_2 = vertices.get(bottomLeft);
		Vertex v_2_3 = vertices.get(bottomRight);
		v_2_1 = processVertex(v_2_1, v_2_1.getTextureIndex(), v_2_1.getNormalIndex(), indices, vertices);
		v_2_2 = processVertex(v_2_2, v_2_2.getTextureIndex(), v_2_2.getNormalIndex(), indices, vertices);
		v_2_3 = processVertex(v_2_3, v_2_3.getTextureIndex(), v_2_3.getNormalIndex(), indices, vertices);
		calculateTangents(v_2_1, v_2_2, v_2_3, textures);
	    }
	}
	removeUnusedVertices(vertices);
	final float[] verticesArray = new float[vertices.size() * Dimension.D3.bases];
	final float[] texturesArray = new float[vertices.size() * 2];
	final float[] normalsArray = new float[vertices.size() * Dimension.D3.bases];
	final float[] tangentsArray = new float[vertices.size() * Dimension.D3.bases];
	final float furthest = convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray,
		normalsArray, tangentsArray);
	final int[] indicesArray = convertIndicesArrayListToArray(indices);
	return new ModelData(verticesArray, texturesArray, normalsArray, tangentsArray, indicesArray, furthest);
    }

    private static Vertex processVertex(Vertex previousVertex, int newTextureIndex, int newNormalIndex,
	    ArrayList<Integer> indices, ArrayList<Vertex> vertices) {
	if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
	    indices.add(previousVertex.getIndex());
	    return previousVertex;
	} else {
	    Vertex anotherVertex = previousVertex.getDuplicateVertex();
	    if (anotherVertex != null) {
		return processVertex(anotherVertex, newTextureIndex, newNormalIndex, indices, vertices);
	    } else {
		Vertex duplicateVertex = previousVertex.duplicate(vertices.size());// NEW
		duplicateVertex.setTextureIndex(newTextureIndex);
		duplicateVertex.setNormalIndex(newNormalIndex);
		previousVertex.setDuplicateVertex(duplicateVertex);
		vertices.add(duplicateVertex);
		indices.add(duplicateVertex.getIndex());
		return duplicateVertex;
	    }
	}
    }

    private static void calculateTangents(Vertex v0, Vertex v1, Vertex v2, ArrayList<Vector2f> textures) {
	Vector3f deltaPos1 = new Vector3f();
	v1.getPosition().sub(v0.getPosition(), deltaPos1);
	Vector3f deltaPos2 = new Vector3f();
	v2.getPosition().sub(v0.getPosition(), deltaPos2);
	Vector2f uv0 = textures.get(v0.getTextureIndex());
	Vector2f uv1 = textures.get(v1.getTextureIndex());
	Vector2f uv2 = textures.get(v2.getTextureIndex());
	Vector2f deltaUv1 = new Vector2f();
	uv1.sub(uv0, deltaUv1);
	Vector2f deltaUv2 = new Vector2f();
	uv2.sub(uv0, deltaUv2);
	float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
	deltaPos1.mul(deltaUv2.y);
	deltaPos2.mul(deltaUv1.y);
	Vector3f tangent = new Vector3f();
	deltaPos1.sub(deltaPos2, tangent);
	tangent.mul(r);
	v0.addTangent(tangent);
	v1.addTangent(tangent);
	v2.addTangent(tangent);
    }

    private static final int[] convertIndicesArrayListToArray(ArrayList<Integer> indices) {
	int[] indicesArray = new int[indices.size()];
	for (int i = 0; i < indicesArray.length; i++) {
	    indicesArray[i] = indices.get(i);
	}
	return indicesArray;
    }

    private static float convertDataToArrays(ArrayList<Vertex> vertices, ArrayList<Vector2f> textures,
	    ArrayList<Vector3f> normals, float[] verticesArray, float[] texturesArray, float[] normalsArray,
	    float[] tangentsArray) {
	float furthestPoint = 0;
	for (int i = 0; i < vertices.size(); i++) {
	    Vertex currentVertex = vertices.get(i);
	    if (currentVertex.getLength() > furthestPoint) {
		furthestPoint = currentVertex.getLength();
	    }
	    Vector3f position = currentVertex.getPosition();
	    Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
	    Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
	    Vector3f tangent = currentVertex.getAverageTangent();
	    verticesArray[i * 3] = position.x;
	    verticesArray[i * 3 + 1] = position.y;
	    verticesArray[i * 3 + 2] = position.z;
	    texturesArray[i * 2] = textureCoord.x;
	    texturesArray[i * 2 + 1] = 1 - textureCoord.y;
	    normalsArray[i * 3] = normalVector.x;
	    normalsArray[i * 3 + 1] = normalVector.y;
	    normalsArray[i * 3 + 2] = normalVector.z;
	    tangentsArray[i * 3] = tangent.x;
	    tangentsArray[i * 3 + 1] = tangent.y;
	    tangentsArray[i * 3 + 2] = tangent.z;
	}
	return furthestPoint;
    }

    private static void removeUnusedVertices(ArrayList<Vertex> vertices) {
	vertices.stream().forEach((vertex) -> {
	    vertex.averageTangents();
	    if (!vertex.isSet()) {
		vertex.setTextureIndex(0);
		vertex.setNormalIndex(0);
	    }
	});
    }

    public final Terrain copy(int gridX, int gridZ) {
	return new Terrain(size, gridX, gridZ, getAdvancedModel().getName(), getAdvancedModel().getModel(), texturePack,
		getAdvancedModel().getMaterial().getTexture(Material.DIFFUSE));
    }

    public final TerrainTexturePack getTexturePack() {
	return texturePack;
    }

    public final Texture getBlendMap() {
	return getAdvancedModel().getMaterial().getTexture(Material.DIFFUSE);
    }

}
