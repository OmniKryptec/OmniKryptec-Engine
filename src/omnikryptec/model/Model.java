package omnikryptec.model;

import java.io.InputStream;
import omnikryptec.loader.ResourceObject;

import omnikryptec.logger.Logger;
import omnikryptec.objConverter.ModelData;
import omnikryptec.objConverter.ObjLoader;
import omnikryptec.util.AdvancedFile;

public class Model implements ResourceObject {

    private final String name;
    private ModelData modelData = null;
    private final VertexArrayObject vao;

    public Model(String name, ModelData modelData) {
        this.modelData = modelData;
        vao = VertexArrayObject.create();
        // vao.storeDataf(modelData.getIndices(), modelData.getVertexCount(),
        // modelData.getVertices(),
        // modelData.getTextureCoords(), modelData.getNormals(),
        // modelData.getTangents());
        vao.storeData(modelData.getIndices(), modelData.getVertexCount(), new DataObject(modelData.getVertices()),
                new DataObject(modelData.getTextureCoords()), new DataObject(modelData.getNormals()),
                new DataObject(modelData.getTangents()));
        this.name = name;
    }

    public Model(String name, VertexArrayObject vao) {
        this.name = name;
        this.vao = vao;
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
