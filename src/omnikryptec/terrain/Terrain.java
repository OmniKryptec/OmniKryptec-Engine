package omnikryptec.terrain;

import omnikryptec.entity.Entity;
import omnikryptec.model.Model;
import omnikryptec.model.TexturedModel;
import omnikryptec.objConverter.ModelData;
import omnikryptec.texture.ITexture;

/**
 * Terrain
 * @author Panzer1119
 */
public class Terrain extends Entity {
    
    public static final TerrainRenderer terrainRenderer = new TerrainRenderer();
    
    private final TerrainTexturePack texturePack;
    
    public Terrain(final float worldX, final float worldZ, ModelData model, TerrainTexturePack texturePack, ITexture blendMap){
    	this(worldX, worldZ, new Model(model), texturePack, blendMap);
    }
    
    public Terrain(final float worldX, final float worldZ, Model model, TerrainTexturePack texturePack, ITexture blendMap){
    	getRelativePos().x = worldX;
    	getRelativePos().z = worldZ;
    	this.texturePack = texturePack;
    	setTexturedModel(new TexturedModel(model, blendMap));
        getTexturedModel().getMaterial().setRenderer(terrainRenderer);
    }
    
    public static final ModelData generateTerrain(final float worldx, final float worldz, final HeightGenerator generator, final float size, final int vertex_count){
         int count = vertex_count * vertex_count;
         float[] vertices = new float[count * 3];
         float[] normals = new float[count * 3];
         float[] textureCoords = new float[count * 2];
         int[] indices = new int[6 * (vertex_count - 1) * (vertex_count - 1)];
         int vertexPointer = 0;
         for(int i = 0; i < vertex_count; i++){
             for(int j = 0; j < vertex_count; j++){
                 vertices[vertexPointer * 3] = (float) (j / ((float) vertex_count - 1) * size);
                 vertices[vertexPointer * 3 + 1] = generator.generateHeight(worldx + j, worldz + i);
                 vertices[vertexPointer * 3 + 2] = (float) (i / ((float) vertex_count - 1) * size);
                 normals[vertexPointer * 3] = 0;
                 normals[vertexPointer * 3 + 1] = 1;
                 normals[vertexPointer * 3 + 2] = 0;
                 textureCoords[vertexPointer * 2] = (float) (j / ((float) vertex_count - 1));
                 textureCoords[vertexPointer * 2 + 1] = (float) (i / ((float) vertex_count - 1));
                 vertexPointer++;
             }
         }
         int pointer = 0;
         for(int gz = 0; gz < (vertex_count - 1); gz++){
             for(int gx = 0; gx < (vertex_count - 1); gx++){
                 int topLeft = (gz * vertex_count) + gx;
                 int topRight = topLeft + 1;
                 int bottomLeft = ((gz + 1) * vertex_count) + gx;
                 int bottomRight = bottomLeft + 1;
                 indices[pointer++] = topLeft;
                 indices[pointer++] = bottomLeft;
                 indices[pointer++] = topRight;
                 indices[pointer++] = topRight;
                 indices[pointer++] = bottomLeft;
                 indices[pointer++] = bottomRight;
             }
         }
         return new ModelData(vertices, textureCoords, normals, normals, indices, 0F);
    }
    
    public final Terrain copy(int gridX, int gridZ) {
        return new Terrain(gridX, gridZ, getTexturedModel().getModel(), texturePack, getTexturedModel().getTexture());
    }

    public final TerrainTexturePack getTexturePack() {
        return texturePack;
    }

    public final ITexture getBlendMap() {
        return getTexturedModel().getTexture();
    }
    
}
