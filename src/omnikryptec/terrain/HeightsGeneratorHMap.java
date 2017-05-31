package omnikryptec.terrain;

import java.awt.image.BufferedImage;

public class HeightsGeneratorHMap implements HeightsGenerator{

    private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;

	private BufferedImage map;
	private float maxheight=1;
	private boolean allowModulo;
	private int width,height;

	
	public HeightsGeneratorHMap(BufferedImage heightsmap, float maxheight, boolean allowModulo) {
		this.map = heightsmap;
		this.maxheight = maxheight;
		this.allowModulo = allowModulo;
		height = map.getHeight();
    	width = map.getWidth();
	}
	
	@Override
	public float generateHeight(float worldx, float worldz) {
		return getHeight(worldx, worldz);
	}
	
	private final float getHeight(float x, float z) {
        if(!allowModulo && (x < 0 || x >= width|| z < 0 || z >= height)) {
            return 0;
        }else if(allowModulo){
        	if(x<0){
        		x = width + x%width; 
        	}else{
        		x %= width;
        	}
        	if(z<0){
        		z = height + z%height;
        	}else{
        		z %= height;
        	}
        }
        float height = map.getRGB((int)x, (int)z);
        height += MAX_PIXEL_COLOR / 2.0F;
        height /= MAX_PIXEL_COLOR / 2.0F;
        height *= maxheight;
        return height;
    }

}
