package omnikryptec.animation.loaders;

import omnikryptec.animation.AnimatedModel;
import omnikryptec.animation.ColladaParser.colladaLoader.ColladaLoader;
import omnikryptec.animation.ColladaParser.dataStructures.AnimatedModelData;
import omnikryptec.animation.ColladaParser.dataStructures.JointData;
import omnikryptec.animation.ColladaParser.dataStructures.MeshData;
import omnikryptec.animation.ColladaParser.dataStructures.SkeletonData;
import omnikryptec.animation.Joint;
import omnikryptec.model.Model;
import omnikryptec.model.VertexArrayObject;
import omnikryptec.objConverter.ModelData;
import omnikryptec.texture.SimpleTexture;
import omnikryptec.texture.Texture;
import omnikryptec.util.Constants;
import omnikryptec.util.MyFile;

public class AnimatedModelLoader {

	/**
	 * Creates an AnimatedEntity from the data in an entity file. It loads up
	 * the collada model data, stores the extracted data in a VAO, sets up the
	 * joint heirarchy, and loads up the entity's texture.
	 * 
	 * @param entityFile
	 *            - the file containing the data for the entity.
	 * @return The animated entity (no animation applied though)
	 */
	public static AnimatedModel loadEntity(MyFile modelFile, MyFile textureFile) {
		AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelFile, Constants.MAX_WEIGHTS);
		ModelData modelData = createModelData(entityData.getMeshData());
		Texture texture = loadTexture(textureFile);
		SkeletonData skeletonData = entityData.getJointsData();
		Joint headJoint = createJoints(skeletonData.headJoint);
		return new AnimatedModel(new Model(modelData), texture, headJoint, skeletonData.jointCount);
	}

	/**
	 * Loads up the diffuse texture for the model.
	 * 
	 * @param textureFile
	 *            - the texture file.
	 * @return The diffuse texture.
	 */
	private static Texture loadTexture(MyFile textureFile) {
		Texture diffuseTexture = SimpleTexture.newTexture(textureFile.getInputStream()).anisotropic().create();
		return diffuseTexture;
	}

	/**
	 * Constructs the joint-hierarchy skeleton from the data extracted from the
	 * collada file.
	 * 
	 * @param data
	 *            - the joints data from the collada file for the head joint.
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
	 * @param data
	 *            - all the data about the mesh that needs to be stored in the
	 *            VAO.
	 * @return The VAO containing all the mesh data for the model.
	 */
	private static ModelData createModelData(MeshData data) {
                ModelData modelData = new ModelData(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getNormals(), data.getIndices(), 0);
		VertexArrayObject vertexArrayObject = VertexArrayObject.create();
                vertexArrayObject.storeData(data.getIndices(), data.getVertexCount(), data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getNormals());
		return modelData;
	}

}
