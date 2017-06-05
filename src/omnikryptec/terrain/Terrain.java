package omnikryptec.terrain;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import omnikryptec.entity.Entity;
import omnikryptec.logger.Logger;
import omnikryptec.model.Model;
import omnikryptec.model.TexturedModel;
import omnikryptec.objConverter.ModelData;
import omnikryptec.texture.ITexture;

/**
 *
 * @author Panzer1119
 */
public class Terrain extends Entity {
    
	/**
	 * made variable
	 */
	@Deprecated
    protected static final float SIZE = 400; //TODO Move this to Constants.java
    
	/**
	 * use {@link HeightsGeneratorHMap}
	 */
	@Deprecated
	private static final float MAX_HEIGHT = 40;
    
    /**
     * moved to {@link HeightsGeneratorHMap}
     */
    @Deprecated
    private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;
    
    public static final TerrainRenderer terrainRenderer = new TerrainRenderer();
    
    //private final float x;
    //private final float z;
    
    //private final Model model;
    private final TerrainTexturePack texturePack;
    
    
    //private final ITexture blendMap;
    
    public Terrain(final float worldx, final float worldz, ModelData model, TerrainTexturePack texturePack, ITexture blendMap){
    	this(worldx, worldz, new Model(model), texturePack, blendMap);
    }
    
    public Terrain(final float worldx, final float worldz, Model model, TerrainTexturePack texturePack, ITexture blendMap){
    	getRelativePos().x = worldx;
    	getRelativePos().z = worldz;
    	this.texturePack = texturePack;
    	setTexturedModel(new TexturedModel(model, blendMap));
        getTexturedModel().getMaterial().setRenderer(terrainRenderer);
    }
    
    @Deprecated
    public Terrain(int gridX, int gridZ, TerrainTexturePack texturePack, ITexture blendMap) {
    	getRelativePos().x = gridX * SIZE;
    	getRelativePos().z = gridZ * SIZE;
        this.texturePack = texturePack;
        setTexturedModel(new TexturedModel(generateTerrain("/omnikryptec/terrain/heightmap.png"), blendMap));
        getTexturedModel().getMaterial().setRenderer(terrainRenderer);
    }
    
    public static final ModelData generateTerrain(final float worldx, final float worldz, final HeightsGenerator generator, final float size, final int vertex_count){
         int count = vertex_count * vertex_count;
         float[] vertices = new float[count * 3];
         float[] normals = new float[count * 3];
         float[] textureCoords = new float[count * 2];
         int[] indices = new int[6 * (vertex_count - 1) * (vertex_count - 1)];
         int vertexPointer = 0;
         for(int i = 0; i < vertex_count; i++){
             for(int j = 0; j < vertex_count; j++){
                 vertices[vertexPointer * 3] = (float) (j / ((float) vertex_count - 1) * size);
                 vertices[vertexPointer * 3 + 1] = generator.generateHeight(worldx, worldz);
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
    
    @Deprecated
    private final Model generateTerrain(String heightMapPath) {
        BufferedImage heightMap = null;
        try {
            heightMap = ImageIO.read(Terrain.class.getResourceAsStream(heightMapPath));
        } catch (Exception ex) {
            Logger.logErr("Error while loading the heightmap: " + ex, ex);
            return null;
        }
        final int VERTEX_COUNT = heightMap.getHeight();
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
        int vertexPointer = 0;
        for(int i = 0; i < VERTEX_COUNT; i++){
            for(int j = 0; j < VERTEX_COUNT; j++){
                vertices[vertexPointer * 3] = (float) (j / ((float) VERTEX_COUNT - 1) * SIZE);
                vertices[vertexPointer * 3 + 1] = getHeight(j, i, heightMap);
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
        return new Model(new ModelData(vertices, textureCoords, normals, normals, indices, 0F));
    }
    
    @Deprecated
    private final float getHeight(int x, int z, BufferedImage heightMap) {
        if(x < 0 || x >= heightMap.getWidth()|| z < 0 || z >= heightMap.getHeight()) {
            return 0;
        }
        float height = heightMap.getRGB(x, z);
        height += MAX_PIXEL_COLOR / 2.0F;
        height /= MAX_PIXEL_COLOR / 2.0F;
        height *= MAX_HEIGHT;
        return height;
    }
    
    @Deprecated
    private final Model generateTerrain() {
        final int VERTEX_COUNT = 128;
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
        return new Model(new ModelData(vertices, textureCoords, normals, normals, indices, 0F));
    }
    
    public final Terrain copy(int gridX, int gridZ) {
        return new Terrain(gridX, gridZ, texturePack, getTexturedModel().getTexture());
    }

    public final TerrainTexturePack getTexturePack() {
        return texturePack;
    }

    public final ITexture getBlendMap() {
        return getTexturedModel().getTexture();
    }
    
}
