package omnikryptec.terrain;

import org.lwjgl.util.vector.Vector3f;

public interface TerrainGenerator {
	
    float generateHeight(float worldX, float worldZ);
	
    default Vector3f generateNormal(float worldX, float worldZ){
    	float hL = generateHeight(worldX-1, worldZ);
    	float hR = generateHeight(worldX+1, worldZ);
    	float hD = generateHeight(worldX, worldZ-1);
    	float hU = generateHeight(worldX, worldZ+1);
    	return (Vector3f) new Vector3f(hL-hR, 2f, hD-hU).normalise();
    }
    
}
