package omnikryptec.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import omnikryptec.logger.Logger;
import omnikryptec.objConverter.ModelData;
import omnikryptec.objConverter.ObjLoader;

public class Model {

    private float radius;
    private VertexArrayObject vao;	
//	public Model(ModelData data){
//		vao = VertexArrayObject.create();
//		vao.storeData(data.getIndices(), data.getIndices().length, data.getVertices(), data.getTextureCoords(), data.getNormals());
//		nm = false;
//	}

    public Model(ModelData data){
        this.radius = data.getFurthestPoint();
        vao = VertexArrayObject.create();
        vao.storeData(data.getIndices(), data.getVertexCount(), data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getTangents());
    }

    public Model(VertexArrayObject vao){
        this.vao = vao;
    }

    public VertexArrayObject getVao(){
        return vao;
    }
    
    public float getRadius() {
        return radius;
    }

    private static final int QUAD_VERTEX_COUNT = 4;
    //private static final float[] QUAD_VERTICES = {0, 0, 1, 0, 1, 1, 0, 1};
    private static final float[] QUAD_VERTICES = { -1, 1, -1, -1, 1, 1, 1, -1 };
    private static final float[] QUAD_TEX_COORDS = {0,0,0,1,1,1,1,0};
    private static final int[] QUAD_INDICES = {0,3,1,1,3,2};

    public static Model generateQuad() {
        VertexArrayObject vao = VertexArrayObject.create();
        vao.storeData(QUAD_INDICES, QUAD_VERTEX_COUNT, QUAD_VERTICES, QUAD_TEX_COORDS);
        return new Model(vao);
    }
    
    public static Model newModel(File file) {
        try {
            return newModel(new FileInputStream(file));
        } catch (Exception ex) {
            Logger.logErr("Error while creating FileInputStream: " + ex, ex);
            return null;
        }
    }
    
    public static Model newModel(String path) {
        try {
            return newModel(Model.class.getResourceAsStream(path));
        } catch (Exception ex) {
            Logger.logErr("Error while creating Stream from path: " + ex, ex);
            return null;
        }
    }
    
    public static Model newModel(InputStream inputStream) {
        return new Model(ObjLoader.loadNMOBJ(inputStream));
    }
    
}
