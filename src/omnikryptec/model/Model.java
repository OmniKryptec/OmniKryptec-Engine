package omnikryptec.model;

import java.io.InputStream;

import org.lwjgl.opengl.GL15;

import omnikryptec.loader.ResourceObject;

import omnikryptec.logger.Logger;
import omnikryptec.objConverter.ModelData;
import omnikryptec.objConverter.ObjLoader;
import omnikryptec.util.AdvancedFile;

public class Model implements ResourceObject {

    private final String name;
    private ModelData modelData = null;
    private final VertexArrayObject vao;
    private VertexBufferObject vbo_updateable;
    
    public Model(String name, ModelData modelData) {
        this.modelData = modelData;
        vao = VertexArrayObject.create();
        vao.storeData(modelData.getIndices(), modelData.getVertexCount(), new DataObject(modelData.getVertices()),
                new DataObject(modelData.getTextureCoords()), new DataObject(modelData.getNormals()),
                new DataObject(modelData.getTangents()));
        this.name = name;
        createVBO();
    }

    //für instanced rendering
    private void createVBO() {
    	vbo_updateable = VertexBufferObject.createEmpty(GL15.GL_ARRAY_BUFFER);
    	vbo_updateable.addInstancedAttribute(getVao(), 4, 4, 20, 0);
    	vbo_updateable.addInstancedAttribute(getVao(), 5, 4, 20, 4);
    	vbo_updateable.addInstancedAttribute(getVao(), 6, 4, 20, 8);
    	vbo_updateable.addInstancedAttribute(getVao(), 7, 4, 20, 12);
    	vbo_updateable.addInstancedAttribute(getVao(), 8, 4, 20, 16);
	}

	public Model(String name, VertexArrayObject vao) {
        this.name = name;
        this.vao = vao;
        createVBO();
    }

	public VertexBufferObject getUpdateableVBO(){
		return vbo_updateable;
	}
	
    public VertexArrayObject getVao() {
        return vao;
    }

    public ModelData getModelData() {
        return modelData;
    }

    public static Model newModel(AdvancedFile file) {
        return newModel("", file);
    }
    
    public static Model newModel(String name, AdvancedFile file) {
        try {
            return newModel(name, file.createInputStream());
        } catch (Exception ex) {
            Logger.logErr("Error while creating FileInputStream: " + ex, ex);
            return null;
        }
    }
    
    public static Model newModel(String path) {
        return newModel("", path);
    }

    public static Model newModel(String name, String path) {
        try {
            return newModel(name, Model.class.getResourceAsStream(path));
        } catch (Exception ex) {
            Logger.logErr("Error while creating Stream from path: " + ex, ex);
            return null;
        }
    }
    
    public static Model newModel(InputStream inputStream) {
        return newModel("", inputStream);
    }

    public static Model newModel(String name, InputStream inputStream) {
        return new Model(name, ObjLoader.loadOBJ(inputStream));
    }

    @Override
    public String getName() {
        return name;
    }

}
