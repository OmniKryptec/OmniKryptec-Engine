package omnikryptec.model;

import java.io.InputStream;

import omnikryptec.logger.Logger;
import omnikryptec.objConverter.ModelData;
import omnikryptec.objConverter.ObjLoader;
import omnikryptec.util.AdvancedFile;

public class Model {

    private ModelData modelData = null;
    private final VertexArrayObject vao;

    public Model(ModelData modelData) {
        this.modelData = modelData;
        vao = VertexArrayObject.create();
        // vao.storeDataf(modelData.getIndices(), modelData.getVertexCount(),
        // modelData.getVertices(),
        // modelData.getTextureCoords(), modelData.getNormals(),
        // modelData.getTangents());
        vao.storeData(modelData.getIndices(), modelData.getVertexCount(), new DataObject(modelData.getVertices()),
                new DataObject(modelData.getTextureCoords()), new DataObject(modelData.getNormals()),
                new DataObject(modelData.getTangents()));
    }

    public Model(VertexArrayObject vao) {
        this.vao = vao;
    }

    public VertexArrayObject getVao() {
        return vao;
    }

    public ModelData getModelData() {
        return modelData;
    }

    public static Model newModel(AdvancedFile file) {
        try {
            return newModel(file.createInputStream());
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
        return new Model(ObjLoader.loadOBJ(inputStream));
    }

}
