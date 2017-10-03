package omnikryptec.util;

import omnikryptec.resource.model.Model;
import omnikryptec.resource.model.Model.VBO_TYPE;
import omnikryptec.resource.model.VertexArrayObject;

public class ModelUtil {

	private static final int QUAD_VERTEX_COUNT = 4;
	// private static final float[] QUAD_VERTICES = {0, 0, 1, 0, 1, 1, 0, 1};
	private static final float[] QUAD_VERTICES = { -1, 1, -1, -1, 1, 1, 1, -1 };
	private static final float[] QUAD_TEX_COORDS = { 0, 0, 0, 1, 1, 1, 1, 0 };
	private static final int[] QUAD_INDICES = { 0, 3, 1, 1, 3, 2 };

	public static Model generateQuad() {
		return generateQuad(VBO_TYPE.NONE);
	}
	
	public static Model generateQuad(VBO_TYPE type) {
		return generateQuad(QUAD_TEX_COORDS, type);
	}

	public static Model generateQuad(float[] texcoords, VBO_TYPE type) {
		VertexArrayObject vao = VertexArrayObject.create();
		vao.storeDataf(QUAD_INDICES, QUAD_VERTEX_COUNT, QUAD_VERTICES, texcoords);
		return new Model("", vao, type);
	}

}
