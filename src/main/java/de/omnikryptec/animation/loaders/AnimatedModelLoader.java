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

package de.omnikryptec.animation.loaders;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.animation.AnimatedModel;
import de.omnikryptec.animation.ColladaParser.colladaLoader.ColladaLoader;
import de.omnikryptec.animation.ColladaParser.dataStructures.AnimatedModelData;
import de.omnikryptec.animation.ColladaParser.dataStructures.JointData;
import de.omnikryptec.animation.ColladaParser.dataStructures.MeshData;
import de.omnikryptec.animation.ColladaParser.dataStructures.SkeletonData;
import de.omnikryptec.animation.Joint;
import de.omnikryptec.renderer.d3.Renderer;
import de.omnikryptec.renderer.d3.RendererRegistration;
import de.omnikryptec.resource.model.Model;
import de.omnikryptec.resource.model.VertexArrayObject;
import de.omnikryptec.resource.texture.SimpleTexture;
import de.omnikryptec.resource.texture.Texture;
import de.omnikryptec.settings.GameSettings;
import de.omnikryptec.util.Instance;

public class AnimatedModelLoader {

    /**
     * Creates an AnimatedEntity from the data in an entity file. It loads up
     * the collada model data, stores the extracted data in a VAO, sets up the
     * joint heirarchy, and loads up the entity's texture.
     *
     * @param modelFile - the file containing the data for the entity.
     * @param textureFile - the file containing the data for the entity.
     * @return The animated entity (no animation applied though)
     */
    public static AnimatedModel loadModel(String name, AdvancedFile modelFile, AdvancedFile textureFile, Renderer renderer) {
        AnimatedModelData entityData = ColladaLoader.loadColladaModel(name, modelFile, Instance.getGameSettings().getInteger(GameSettings.ANIMATION_MAX_WEIGHTS));
        MeshData meshData = entityData.getMeshData();
        Model model = new Model(name, meshData);
        VertexArrayObject vao = model.getVao();
        vao.bind();
        vao.createIntAttribute(4, meshData.getJointIds(), 3);
        vao.createAttribute(5, meshData.getVertexWeights(), 3);
        Texture texture = loadTexture(name, textureFile);
        SkeletonData skeletonData = entityData.getJointsData();
        Joint headJoint = createJoints(skeletonData.headJoint);
        AnimatedModel animatedModel = new AnimatedModel(modelFile.getName(), model, texture, headJoint, skeletonData.jointCount);
        animatedModel.getMaterial().setRenderer(renderer == null ? RendererRegistration.DEF_ANIMATEDMODEL_RENDERER : renderer);
        return animatedModel;
    }

    public static AnimatedModel createModel(String name, AnimatedModelData entityData, Texture texture, Renderer renderer) {
        MeshData meshData = entityData.getMeshData();
        Model model = new Model(name, meshData);
        VertexArrayObject vao = model.getVao();
        vao.bind();
        vao.createIntAttribute(4, meshData.getJointIds(), 3);
        vao.createAttribute(5, meshData.getVertexWeights(), 3);
        SkeletonData skeletonData = entityData.getJointsData();
        Joint headJoint = createJoints(skeletonData.headJoint);
        AnimatedModel animatedModel = new AnimatedModel(name, model, texture, headJoint, skeletonData.jointCount);
        animatedModel.getMaterial().setRenderer(renderer == null ? RendererRegistration.DEF_ANIMATEDMODEL_RENDERER : renderer);
        return animatedModel;
    }

    /**
     * Loads up the diffuse texture for the model.
     *
     * @param textureFile - the texture file.
     * @return The diffuse texture.
     */
    private static Texture loadTexture(String name, AdvancedFile textureFile) {
        Texture diffuseTexture = SimpleTexture.newTextureb(name, textureFile.createInputStream()).anisotropic().create();
        return diffuseTexture;
    }

    /**
     * Constructs the joint-hierarchy skeleton from the data extracted from the
     * collada file.
     *
     * @param data - the joints data from the collada file for the head joint.
     * @return The created joint, with all its descendants added.
     */
    private static Joint createJoints(JointData data) {
        Joint joint = new Joint(data.index, data.nameId, data.bindLocalTransform);
        data.children.stream().forEach((child) -> {
            joint.addChild(createJoints(child));
        });
        return joint;
    }

//    /**
//     * Stores the mesh data in a VAO.
//     *
//     * @param data - all the data about the mesh that needs to be stored in the
//     * VAO.
//     * @return The VAO containing all the mesh data for the model.
//     */
//    private static VertexArrayObject createVertexArrayObject(MeshData data) {
//        VertexArrayObject vao = VertexArrayObject.create();
//        vao.bind();
//        vao.createIndexBuffer(data.getIndices());
//        vao.createAttribute(0, data.getVertices(), 3);
//        vao.createAttribute(1, data.getTextureCoords(), 2);
//        vao.createAttribute(2, data.getNormals(), 3);
//        vao.createAttribute(3, data.getTangents(), 3);
//        vao.createIntAttribute(4, data.getJointIds(), 3);
//        vao.createAttribute(5, data.getVertexWeights(), 3);
//        return vao;
//    }
}
