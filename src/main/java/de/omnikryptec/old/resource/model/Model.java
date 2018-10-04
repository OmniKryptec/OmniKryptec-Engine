/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.resource.model;

import java.io.InputStream;

import org.lwjgl.opengl.GL15;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.old.resource.loader.ResourceObject;
import de.omnikryptec.old.resource.objConverter.ModelData;
import de.omnikryptec.old.resource.objConverter.ObjLoader;
import de.omnikryptec.old.util.logger.Logger;

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
