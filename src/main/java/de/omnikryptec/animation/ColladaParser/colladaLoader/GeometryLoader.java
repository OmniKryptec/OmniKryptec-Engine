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

package de.omnikryptec.animation.ColladaParser.colladaLoader;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import de.omnikryptec.animation.ColladaParser.dataStructures.AnimatedVertex;
import de.omnikryptec.animation.ColladaParser.dataStructures.MeshData;
import de.omnikryptec.animation.ColladaParser.dataStructures.VertexSkinData;
import de.omnikryptec.util.XMLUtil;

/**
 * Loads the mesh data for a model from a collada XML file.
 *
 * @author Karl &amp; Panzer1119
 */
public class GeometryLoader {

    private final Element meshData;

    private final List<VertexSkinData> vertexWeights;

    private float[] verticesArray;
    private float[] normalsArray;
    private float[] tangentsArray;
    private float[] texturesArray;
    private int[] indicesArray;
    private int[] jointIdsArray;
    private float[] weightsArray;

    List<AnimatedVertex> vertices = new ArrayList<>();
    List<Vector2f> textures = new ArrayList<>();
    List<Vector3f> normals = new ArrayList<>();
    List<Integer> indices = new ArrayList<>();

    public GeometryLoader(Element geometryNode, List<VertexSkinData> vertexWeights) {
        this.vertexWeights = vertexWeights;
        this.meshData = XMLUtil.getChild(XMLUtil.getChild(geometryNode, "geometry"), "mesh");
    }

    public final MeshData extractModelData() {
        readRawData();
        assembleVertices();
        removeUnusedVertices();
        initArrays();
        calculateTangents();
        convertDataToArrays();
        convertIndicesListToArray();
        return new MeshData(verticesArray, texturesArray, normalsArray, tangentsArray, indicesArray, jointIdsArray, weightsArray);
    }

    private void readRawData() {
        readPositions();
        readNormals();
        readTextureCoords();
    }

    private void readPositions() {
        final String positionsId = XMLUtil.getChild(XMLUtil.getChild(meshData, "vertices"), "input").getAttributeValue("source").substring(1);
        final Element positionsData = XMLUtil.getChild(XMLUtil.getChildWithAttribute(meshData, "source", "id", positionsId), "float_array");
        final int count = Integer.parseInt(positionsData.getAttributeValue("count"));
        final String[] posData = positionsData.getText().split(" ");
        for (int i = 0; i < count / 3; i++) {
            float x = Float.parseFloat(posData[i * 3]);
            float y = Float.parseFloat(posData[i * 3 + 1]);
            float z = Float.parseFloat(posData[i * 3 + 2]);
            Vector4f position = new Vector4f(x, y, z, 1);
            ColladaLoader.CORRECTION.transform(position, position);
            vertices.add(new AnimatedVertex(vertices.size(), new Vector3f(position.x, position.y, position.z), vertexWeights.get(vertices.size())));
        }
    }

    private void readNormals() {
        final String normalsId = XMLUtil.getChildWithAttribute(XMLUtil.getChild(meshData, "polylist"), "input", "semantic", "NORMAL").getAttributeValue("source").substring(1);
        final Element normalsData = XMLUtil.getChild(XMLUtil.getChildWithAttribute(meshData, "source", "id", normalsId), "float_array");
        final int count = Integer.parseInt(normalsData.getAttributeValue("count"));
        final String[] normData = normalsData.getText().split(" ");
        for (int i = 0; i < count / 3; i++) {
            float x = Float.parseFloat(normData[i * 3]);
            float y = Float.parseFloat(normData[i * 3 + 1]);
            float z = Float.parseFloat(normData[i * 3 + 2]);
            Vector4f norm = new Vector4f(x, y, z, 0f);
            ColladaLoader.CORRECTION.transform(norm, norm);
            normals.add(new Vector3f(norm.x, norm.y, norm.z));
        }
    }

    private void readTextureCoords() {
        final String texCoordsId = XMLUtil.getChildWithAttribute(XMLUtil.getChild(meshData, "polylist"), "input", "semantic", "TEXCOORD").getAttributeValue("source").substring(1);
        final Element texCoordsData = XMLUtil.getChild(XMLUtil.getChildWithAttribute(meshData, "source", "id", texCoordsId), "float_array");
        final int count = Integer.parseInt(texCoordsData.getAttributeValue("count"));
        final String[] texData = texCoordsData.getText().split(" ");
        for (int i = 0; i < count / 2; i++) {
            float s = Float.parseFloat(texData[i * 2]);
            float t = Float.parseFloat(texData[i * 2 + 1]);
            textures.add(new Vector2f(s, t));
        }
    }

    private void assembleVertices() {
        final Element poly = XMLUtil.getChild(meshData, "polylist");
        final int typeCount = XMLUtil.getChildren(poly, "input").size();
        final String[] indexData = XMLUtil.getChild(poly, "p").getText().split(" ");
        for (int i = 0; i < indexData.length / typeCount; i++) {
            int positionIndex = Integer.parseInt(indexData[i * typeCount]);
            int normalIndex = Integer.parseInt(indexData[i * typeCount + 1]);
            int texCoordIndex = Integer.parseInt(indexData[i * typeCount + 2]);
            processVertex(positionIndex, normalIndex, texCoordIndex);
        }
    }

    private AnimatedVertex processVertex(int posIndex, int normIndex, int texIndex) {
        AnimatedVertex currentVertex = vertices.get(posIndex);
        if (!currentVertex.isSet()) {
            currentVertex.setTextureIndex(texIndex);
            currentVertex.setNormalIndex(normIndex);
            indices.add(posIndex);
            return currentVertex;
        } else {
            return dealWithAlreadyProcessedVertex(currentVertex, texIndex, normIndex);
        }
    }

    private int[] convertIndicesListToArray() {
        this.indicesArray = new int[indices.size()];
        for (int i = 0; i < indicesArray.length; i++) {
            indicesArray[i] = indices.get(i);
        }
        return indicesArray;
    }

    private void calculateTangents() {
        for (int i = 0; i < indices.size() - 2; i += 3) {
            int i_1 = indices.get(i);
            int i_2 = indices.get(i + 1);
            int i_3 = indices.get(i + 2);
            AnimatedVertex v_1 = vertices.get(i_1);
            AnimatedVertex v_2 = vertices.get(i_2);
            AnimatedVertex v_3 = vertices.get(i_3);
            calculateTangents(v_1, v_2, v_3, textures);
        }
    }

    private static void calculateTangents(AnimatedVertex v0, AnimatedVertex v1, AnimatedVertex v2, List<Vector2f> textures) {
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

    private float convertDataToArrays() {
        float furthestPoint = 0;
        for (int i = 0; i < vertices.size(); i++) {
            AnimatedVertex currentVertex = vertices.get(i);
            if (currentVertex.getLengthSquared() > furthestPoint) {
                furthestPoint = currentVertex.getLengthSquared();
            }
            Vector3f position = currentVertex.getPosition();
            Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
            Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
            Vector3f tangentVector = currentVertex.getAverageTangent();
            verticesArray[i * 3] = position.x;
            verticesArray[i * 3 + 1] = position.y;
            verticesArray[i * 3 + 2] = position.z;
            texturesArray[i * 2] = textureCoord.x;
            texturesArray[i * 2 + 1] = 1 - textureCoord.y;
            normalsArray[i * 3] = normalVector.x;
            normalsArray[i * 3 + 1] = normalVector.y;
            normalsArray[i * 3 + 2] = normalVector.z;
            tangentsArray[i * 3] = tangentVector.x;
            tangentsArray[i * 3 + 1] = tangentVector.y;
            tangentsArray[i * 3 + 2] = tangentVector.z;
            VertexSkinData weights = currentVertex.getWeightsData();
            jointIdsArray[i * 3] = weights.jointIds.get(0);
            jointIdsArray[i * 3 + 1] = weights.jointIds.get(1);
            jointIdsArray[i * 3 + 2] = weights.jointIds.get(2);
            weightsArray[i * 3] = weights.weights.get(0);
            weightsArray[i * 3 + 1] = weights.weights.get(1);
            weightsArray[i * 3 + 2] = weights.weights.get(2);

        }
        return furthestPoint;
    }

    private AnimatedVertex dealWithAlreadyProcessedVertex(AnimatedVertex previousVertex, int newTextureIndex,
            int newNormalIndex) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.getIndex());
            return previousVertex;
        } else {
            AnimatedVertex anotherVertex = (AnimatedVertex) previousVertex.getDuplicateVertex();
            if (anotherVertex != null) {
                return dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex);
            } else {
                AnimatedVertex duplicateVertex = new AnimatedVertex(vertices.size(), previousVertex.getPosition(),
                        previousVertex.getWeightsData());
                duplicateVertex.setTextureIndex(newTextureIndex);
                duplicateVertex.setNormalIndex(newNormalIndex);
                previousVertex.setDuplicateVertex(duplicateVertex);
                vertices.add(duplicateVertex);
                indices.add(duplicateVertex.getIndex());
                return duplicateVertex;
            }

        }
    }

    private void initArrays() {
        this.verticesArray = new float[vertices.size() * 3];
        this.texturesArray = new float[vertices.size() * 2];
        this.normalsArray = new float[vertices.size() * 3];
        this.tangentsArray = new float[vertices.size() * 3];
        this.jointIdsArray = new int[vertices.size() * 3];
        this.weightsArray = new float[vertices.size() * 3];
    }

    private void removeUnusedVertices() {
        vertices.stream().map((vertex) -> {
            vertex.averageTangents();
            return vertex;
        }).filter((vertex) -> (!vertex.isSet())).map((vertex) -> {
            vertex.setTextureIndex(0);
            return vertex;
        }).forEach((vertex) -> {
            vertex.setNormalIndex(0);
        });
    }

}
