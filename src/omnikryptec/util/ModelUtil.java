package omnikryptec.util;

import omnikryptec.model.Model;
import omnikryptec.model.VertexArrayObject;

public class ModelUtil {

	private static final int QUAD_VERTEX_COUNT = 4;
    //private static final float[] QUAD_VERTICES = {0, 0, 1, 0, 1, 1, 0, 1};
    private static final float[] QUAD_VERTICES = { -1, 1, -1, -1, 1, 1, 1, -1 };
    private static final float[] QUAD_TEX_COORDS = {0,0,0,1,1,1,1,0};
    private static final int[] QUAD_INDICES = {0,3,1,1,3,2};

    public static Model generateQuad(){
    	return generateQuad(QUAD_TEX_COORDS);
    }
    
    public static Model generateQuad(float[] texcoords) {
        VertexArrayObject vao = VertexArrayObject.create();
        vao.storeData(QUAD_INDICES, QUAD_VERTEX_COUNT, QUAD_VERTICES, texcoords);
        return new Model(vao);
    }
	
}
