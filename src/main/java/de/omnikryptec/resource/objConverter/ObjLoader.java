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

package de.omnikryptec.resource.objConverter;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.util.logger.Logger;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ObjLoader {

    public static ModelData loadOBJ(AdvancedFile file) {
        return loadOBJ(file.createInputStream());
    }

    public static ModelData loadOBJ(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream);
        } catch (Exception ex) {
            Logger.logErr("Error while creating InputStreamReader to load an .obj: " + ex, ex);
            return null;
        }
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = "";
        final ArrayList<Vertex> vertices = new ArrayList<>();
        final ArrayList<Vector2f> textures = new ArrayList<>();
        final ArrayList<Vector3f> normals = new ArrayList<>();
        final ArrayList<Integer> indices = new ArrayList<>();
        try {
            while (true) {
                line = bufferedReader.readLine();
                if (line == null) {
                    break;
                } else if (line.startsWith("v ")) {
                    String[] currentLine = line.split(" ");
                    Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]),
                            (float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
                    Vertex newVertex = new Vertex(vertices.size(), vertex);
                    vertices.add(newVertex);
                } else if (line.startsWith("vt ")) {
                    String[] currentLine = line.split(" ");
                    Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
                            (float) Float.valueOf(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) {
                    String[] currentLine = line.split(" ");
                    Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]),
                            (float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
                    normals.add(normal);
                } else if (line.startsWith("f ")) {
                    break;
                }
            }
            while (line != null) {
                if (!line.startsWith("f ")) {
                    line = bufferedReader.readLine();
                    continue;
                }
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");
                Vertex v0 = processVertex(vertex1, vertices, indices);
                Vertex v1 = processVertex(vertex2, vertices, indices);
                Vertex v2 = processVertex(vertex3, vertices, indices);
                calculateTangents(v0, v1, v2, textures);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        } catch (Exception ex) {
            Logger.logErr("Error while processing .obj: " + ex, ex);
            return null;
        }
        removeUnusedVertices(vertices);
        float[] verticesArray = new float[vertices.size() * 3];
        float[] texturesArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];
        float[] tangentsArray = new float[vertices.size() * 3];
        float furthest = convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray, normalsArray,
                tangentsArray);
        int[] indicesArray = convertIndicesArrayListToArray(indices);
        return new ModelData(verticesArray, texturesArray, normalsArray, tangentsArray, indicesArray, furthest);
    }

//    @Deprecated
//    private static ModelData OLDloadNMOBJ(InputStream file) {
//        InputStreamReader objStream = null;
//        try {
//            objStream = new InputStreamReader(file);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        BufferedReader reader = new BufferedReader(objStream);
//        String line;
//        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
//        ArrayList<Vector2f> textures = new ArrayList<Vector2f>();
//        ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
//        ArrayList<Integer> indices = new ArrayList<Integer>();
//        try {
//            while (true) {
//                line = reader.readLine();
//                if (line.startsWith("v ")) {
//                    String[] currentLine = line.split(" ");
//                    Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]),
//                            (float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
//                    Vertex newVertex = new Vertex(vertices.size(), vertex);
//                    vertices.add(newVertex);
//
//                } else if (line.startsWith("vt ")) {
//                    String[] currentLine = line.split(" ");
//                    Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
//                            (float) Float.valueOf(currentLine[2]));
//                    textures.add(texture);
//                } else if (line.startsWith("vn ")) {
//                    String[] currentLine = line.split(" ");
//                    Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]),
//                            (float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
//                    normals.add(normal);
//                } else if (line.startsWith("f ")) {
//                    break;
//                }
//            }
//            while (line != null && line.startsWith("f ")) {
//                String[] currentLine = line.split(" ");
//                String[] vertex1 = currentLine[1].split("/");
//                String[] vertex2 = currentLine[2].split("/");
//                String[] vertex3 = currentLine[3].split("/");
//                Vertex v0 = processVertex(vertex1, vertices, indices);
//                Vertex v1 = processVertex(vertex2, vertices, indices);
//                Vertex v2 = processVertex(vertex3, vertices, indices);
//                calculateTangents(v0, v1, v2, textures);
//                line = reader.readLine();
//            }
//            reader.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        removeUnusedVertices(vertices);
//        float[] verticesArray = new float[vertices.size() * 3];
//        float[] texturesArray = new float[vertices.size() * 2];
//        float[] normalsArray = new float[vertices.size() * 3];
//        float[] tangentsArray = new float[vertices.size() * 3];
//        float furthest = convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray, normalsArray,
//                tangentsArray);
//        int[] indicesArray = convertIndicesArrayListToArray(indices);
//        return new ModelData(verticesArray, texturesArray, normalsArray, tangentsArray, indicesArray, furthest);
//    }
    private static void calculateTangents(Vertex v0, Vertex v1, Vertex v2, ArrayList<Vector2f> textures) {
        Vector3f delatPos1 = v1.getPosition().sub(v0.getPosition(), new Vector3f());
        Vector3f delatPos2 = v2.getPosition().sub(v0.getPosition(), new Vector3f());
        Vector2f uv0 = textures.get(v0.getTextureIndex());
        Vector2f uv1 = textures.get(v1.getTextureIndex());
        Vector2f uv2 = textures.get(v2.getTextureIndex());
        Vector2f deltaUv1 = uv1.sub(uv0, new Vector2f());
        Vector2f deltaUv2 = uv2.sub(uv0, new Vector2f());
        float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
        delatPos1.mul(deltaUv2.y);
        delatPos2.mul(deltaUv1.y);
        Vector3f tangent = delatPos1.sub(delatPos2, new Vector3f());
        tangent.mul(r);
        v0.addTangent(tangent);
        v1.addTangent(tangent);
        v2.addTangent(tangent);
    }

    private static Vertex processVertex(String[] vertex, ArrayList<Vertex> vertices, ArrayList<Integer> indices) {
        int index = Integer.parseInt(vertex[0]) - 1;
        Vertex currentVertex = vertices.get(index);
        int textureIndex = Integer.parseInt(vertex[1]) - 1;
        int normalIndex = Integer.parseInt(vertex[2]) - 1;
        if (!currentVertex.isSet()) {
            currentVertex.setTextureIndex(textureIndex);
            currentVertex.setNormalIndex(normalIndex);
            indices.add(index);
            return currentVertex;
        } else {
            return dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices, vertices);
        }
    }

    private static int[] convertIndicesArrayListToArray(ArrayList<Integer> indices) {
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

    private static Vertex dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex, int newNormalIndex,
            ArrayList<Integer> indices, ArrayList<Vertex> vertices) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.getIndex());
            return previousVertex;
        } else {
            Vertex anotherVertex = previousVertex.getDuplicateVertex();
            if (anotherVertex != null) {
                return dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex, indices,
                        vertices);
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

    private static void removeUnusedVertices(ArrayList<Vertex> vertices) {
        for (Vertex vertex : vertices) {
            vertex.averageTangents();
            if (!vertex.isSet()) {
                vertex.setTextureIndex(0);
                vertex.setNormalIndex(0);
            }
        }
    }

}
