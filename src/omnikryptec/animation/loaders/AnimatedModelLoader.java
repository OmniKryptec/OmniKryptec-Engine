package omnikryptec.animation.loaders;

import omnikryptec.animation.AnimatedModel;
import omnikryptec.animation.Joint;
import omnikryptec.animation.ColladaParser.colladaLoader.ColladaLoader;
import omnikryptec.animation.ColladaParser.dataStructures.AnimatedModelData;
import omnikryptec.animation.ColladaParser.dataStructures.JointData;
import omnikryptec.animation.ColladaParser.dataStructures.MeshData;
import omnikryptec.animation.ColladaParser.dataStructures.SkeletonData;
import omnikryptec.model.Model;
import omnikryptec.model.VertexArrayObject;
import omnikryptec.renderer.Renderer;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.texture.SimpleTexture;
import omnikryptec.texture.Texture;
import omnikryptec.util.AdvancedFile;
import omnikryptec.util.Instance;

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
    public static AnimatedModel loadModel(AdvancedFile modelFile, AdvancedFile textureFile, Renderer renderer) {
        AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelFile, Instance.MAX_WEIGHTS);
        Model model = new Model(createVertexArrayObject(entityData.getMeshData()));
        Texture texture = loadTexture(textureFile);
        SkeletonData skeletonData = entityData.getJointsData();
        Joint headJoint = createJoints(skeletonData.headJoint);
        AnimatedModel animatedModel = new AnimatedModel(model, texture, headJoint, skeletonData.jointCount);
        animatedModel.getMaterial().setRenderer(renderer == null ? RendererRegistration.DEF_ANIMATEDMODEL_RENDERER : renderer);
        return animatedModel;
    }

    /**
     * Loads up the diffuse texture for the model.
     *
     * @param textureFile - the texture file.
     * @return The diffuse texture.
     */
    private static Texture loadTexture(AdvancedFile textureFile) {
        Texture diffuseTexture = SimpleTexture.newTextureb(textureFile.createInputStream()).anisotropic().create();
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
        for (JointData child : data.children) {
            joint.addChild(createJoints(child));
        }
        return joint;
    }

    /**
     * Stores the mesh data in a VAO.
     *
     * @param data - all the data about the mesh that needs to be stored in the
     * VAO.
     * @return The VAO containing all the mesh data for the model.
     */
    private static VertexArrayObject createVertexArrayObject(MeshData data) {
        VertexArrayObject vao = VertexArrayObject.create();
        vao.bind();
        vao.createIndexBuffer(data.getIndices());
        vao.createAttribute(0, data.getVertices(), 3);
        vao.createAttribute(1, data.getTextureCoords(), 2);
        vao.createAttribute(2, data.getNormals(), 3);
        vao.createAttribute(3, data.getTangents(), 3);
        vao.createIntAttribute(4, data.getJointIds(), 3);
        vao.createAttribute(5, data.getVertexWeights(), 3);
        vao.unbind();
        return vao;
    }

}
