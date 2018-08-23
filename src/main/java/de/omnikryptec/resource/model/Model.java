package de.omnikryptec.resource.model;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.resource.loader.ResourceObject;
import de.omnikryptec.resource.objConverter.ModelData;
import de.omnikryptec.resource.objConverter.ObjLoader;
import de.omnikryptec.util.logger.Logger;
import org.lwjgl.opengl.GL15;

import java.io.InputStream;

public class Model implements ResourceObject {

    public static enum VBO_TYPE {
        NONE, SPRITEBATCH, RENDERING;
    }

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
        createVBO(VBO_TYPE.RENDERING);
    }

    //fuer instanced rendering
    private void createVBO(VBO_TYPE t) {
        if (t == VBO_TYPE.NONE) {
            return;
        }
        vbo_updateable = VertexBufferObject.create(GL15.GL_ARRAY_BUFFER);
        if (t == VBO_TYPE.RENDERING) {
            int i = 20;
            vbo_updateable.addInstancedAttribute(getVao(), 4, 4, i, 0);
            vbo_updateable.addInstancedAttribute(getVao(), 5, 4, i, 4);
            vbo_updateable.addInstancedAttribute(getVao(), 6, 4, i, 8);
            vbo_updateable.addInstancedAttribute(getVao(), 7, 4, i, 12);
            vbo_updateable.addInstancedAttribute(getVao(), 8, 4, i, 16);
        }
    }

    public Model(String name, VertexArrayObject vao, VBO_TYPE type) {
        this.name = name;
        this.vao = vao;
        createVBO(type);
    }

    public VertexBufferObject getUpdateableVBO() {
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

	@Override
	public ResourceObject delete() {
		vao.delete();
		vbo_updateable.delete();
		return this;
	}

}
