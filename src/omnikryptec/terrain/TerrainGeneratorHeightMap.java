package omnikryptec.terrain;

import java.awt.image.BufferedImage;

public class TerrainGeneratorHeightMap implements TerrainGenerator{

    private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;

    private final BufferedImage map;
    private final float maxHeight;
    private final boolean allowModulo;
    private final int width, height;

    public TerrainGeneratorHeightMap(BufferedImage heightsmap, float maxHeight, boolean allowModulo) {
        this.map = heightsmap;
        this.maxHeight = maxHeight;
        this.allowModulo = allowModulo;
        height = map.getHeight();
        width = map.getWidth();
    }
	
    @Override
    public final float generateHeight(float worldX, float worldZ) {
        return getHeight(worldX, worldZ);
    }

    private final float getHeight(float x, float z) {
        if(!allowModulo && (x < 0 || x >= width || z < 0 || z >= height)) {
            return 0;
        } else if(allowModulo) {
            if(x < 0) {
                x = width + x % width; 
            }
            x %= width;
            if(z < 0) {
                z = height + z % height;
            }
            z %= height;
        }
        float height = map.getRGB((int) x, (int) z);
        height += MAX_PIXEL_COLOR / 2.0F;
        height /= MAX_PIXEL_COLOR / 2.0F;
        height *= maxHeight;
        return height;
    }

}
