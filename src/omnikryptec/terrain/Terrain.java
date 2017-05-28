package omnikryptec.terrain;

import omnikryptec.entity.Entity;
import omnikryptec.model.Model;
import omnikryptec.model.TexturedModel;
import omnikryptec.objConverter.ModelData;
import omnikryptec.texture.Texture;

/**
 *
 * @author Panzer1119
 */
public class Terrain extends Entity {
    
    private static final float SIZE = 800; //TODO Move this to Constants.java
    private static final int VERTEX_COUNT = 128; //TODO Move this to Constants.java
    
    public static final TerrainRenderer terrainRenderer = new TerrainRenderer();
    
    private final float x;
    private final float z;
    
    public Terrain(int gridX, int gridZ, Texture texture) {
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        setTexturedModel(generateTerrain(texture));
        getTexturedModel().getMaterial().setRenderer(terrainRenderer);
    }
    
    private final TexturedModel generateTerrain(Texture texture) {
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
        int vertexPointer = 0;
        for(int i = 0; i < VERTEX_COUNT; i++){
            for(int j = 0; j < VERTEX_COUNT; j++){
                vertices[vertexPointer * 3] = (float) (j / ((float) VERTEX_COUNT - 1) * SIZE);
                vertices[vertexPointer * 3 + 1] = 0;
                vertices[vertexPointer * 3 + 2] = (float) (i / ((float) VERTEX_COUNT - 1) * SIZE);
                normals[vertexPointer * 3] = 0;
                normals[vertexPointer * 3 + 1] = 1;
                normals[vertexPointer * 3 + 2] = 0;
                textureCoords[vertexPointer * 2] = (float) (j / ((float) VERTEX_COUNT - 1));
                textureCoords[vertexPointer * 2 + 1] = (float) (i / ((float) VERTEX_COUNT - 1));
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz = 0; gz < (VERTEX_COUNT - 1); gz++){
            for(int gx = 0; gx < (VERTEX_COUNT - 1); gx++){
                int topLeft = (gz * VERTEX_COUNT) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return new TexturedModel(new Model(new ModelData(vertices, textureCoords, normals, normals, indices, 0F)), texture);
    }

    public final float getX() {
        return x;
    }

    public final float getZ() {
        return z;
    }
    
}