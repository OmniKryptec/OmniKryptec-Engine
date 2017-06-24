package omnikryptec.terrain;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import omnikryptec.entity.Entity;
import omnikryptec.model.Model;
import omnikryptec.model.TexturedModel;
import omnikryptec.objConverter.ModelData;
import omnikryptec.objConverter.Vertex;
import omnikryptec.texture.Texture;

/**
 * Terrain
 *
 * @author Panzer1119
 */
public class Terrain extends Entity {

    public static final TerrainRenderer terrainRenderer = new TerrainRenderer();

    private final TerrainTexturePack texturePack;

    public Terrain(final float worldX, final float worldZ, String texturedModelName, ModelData model, TerrainTexturePack texturePack, Texture blendMap) {
        this(worldX, worldZ, texturedModelName, new Model(model), texturePack, blendMap);
    }

    public Terrain(final float worldX, final float worldZ, String texturedModelName, Model model, TerrainTexturePack texturePack, Texture blendMap) {
        getRelativePos().x = worldX;
        getRelativePos().z = worldZ;
        this.texturePack = texturePack;
        setAdvancedModel(new TexturedModel(texturedModelName, model, blendMap));
        getAdvancedModel().getMaterial().setRenderer(terrainRenderer);
    }

    public static final ModelData generateTerrain(final float worldx, final float worldz, final TerrainGenerator generator, final float size, final int vertex_count) {
        final ArrayList<Vertex> vertices = new ArrayList<>();
        final ArrayList<Vector2f> textures = new ArrayList<>();
        final ArrayList<Vector3f> normals = new ArrayList<>();
        final ArrayList<Integer> indices = new ArrayList<>();
        Vector3f normal;
        Vector2f texture;
        for (int i = 0; i < vertex_count; i++) {
            for (int j = 0; j < vertex_count; j++) {
                final Vertex vertex = new Vertex(vertices.size(),
                        new Vector3f((float) (j / ((float) vertex_count - 1) * size),
                                (float) generator.getHeight(worldx + j, worldz + i),
                                (float) (i / ((float) vertex_count - 1) * size)));
                vertices.add(vertex);
                normal = generator.generateNormal(worldx + j, worldz + i);
                vertex.setNormalIndex(normals.size());
                normals.add(normal);
                texture = new Vector2f((float) (j / ((float) vertex_count - 1)),
                        (float) (i / ((float) vertex_count - 1)));
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
        final float[] verticesArray = new float[vertices.size() * 3];
        final float[] texturesArray = new float[vertices.size() * 2];
        final float[] normalsArray = new float[vertices.size() * 3];
        final float[] tangentsArray = new float[vertices.size() * 3];
        final float furthest = convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray,
                normalsArray, tangentsArray);
        final int[] indicesArray = convertIndicesArrayListToArray(indices);
        return new ModelData(verticesArray, texturesArray, normalsArray, tangentsArray, indicesArray, furthest);
    }

    private static Vertex processVertex(Vertex previousVertex, int newTextureIndex, int newNormalIndex, ArrayList<Integer> indices, ArrayList<Vertex> vertices) {
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

    private static final void calculateTangents(Vertex v0, Vertex v1, Vertex v2, ArrayList<Vector2f> textures) {
        Vector3f delatPos1 = Vector3f.sub(v1.getPosition(), v0.getPosition(), null);
        Vector3f delatPos2 = Vector3f.sub(v2.getPosition(), v0.getPosition(), null);
        Vector2f uv0 = textures.get(v0.getTextureIndex());
        Vector2f uv1 = textures.get(v1.getTextureIndex());
        Vector2f uv2 = textures.get(v2.getTextureIndex());
        Vector2f deltaUv1 = Vector2f.sub(uv1, uv0, null);
        Vector2f deltaUv2 = Vector2f.sub(uv2, uv0, null);
        float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
        delatPos1.scale(deltaUv2.y);
        delatPos2.scale(deltaUv1.y);
        Vector3f tangent = Vector3f.sub(delatPos1, delatPos2, null);
        tangent.scale(r);
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

    private static float convertDataToArrays(ArrayList<Vertex> vertices, ArrayList<Vector2f> textures, ArrayList<Vector3f> normals, float[] verticesArray, float[] texturesArray, float[] normalsArray, float[] tangentsArray) {
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
        return new Terrain(gridX, gridZ, getAdvancedModel().getName(), getAdvancedModel().getModel(), texturePack, getAdvancedModel().getTexture());
    }

    public final TerrainTexturePack getTexturePack() {
        return texturePack;
    }

    public final Texture getBlendMap() {
        return getAdvancedModel().getTexture();
    }

}
