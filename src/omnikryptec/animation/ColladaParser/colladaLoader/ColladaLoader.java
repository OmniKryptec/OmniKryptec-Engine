package omnikryptec.animation.ColladaParser.colladaLoader;

import omnikryptec.animation.ColladaParser.dataStructures.AnimatedModelData;
import omnikryptec.animation.ColladaParser.dataStructures.AnimationData;
import omnikryptec.animation.ColladaParser.dataStructures.MeshData;
import omnikryptec.animation.ColladaParser.dataStructures.SkeletonData;
import omnikryptec.animation.ColladaParser.dataStructures.SkinningData;
import omnikryptec.animation.ColladaParser.xmlParser.XmlNode;
import omnikryptec.animation.ColladaParser.xmlParser.XmlParser;
import omnikryptec.util.AdvancedFile;

public class ColladaLoader {

	public static MeshData getMeshDataOnly(AdvancedFile colladaFile) {
		return loadColladaModel(colladaFile, 0).getMeshData();
	}

	public static AnimatedModelData loadColladaModel(AdvancedFile colladaFile, int maxWeights) {
		XmlNode node = XmlParser.loadXmlFile(colladaFile);

		SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), maxWeights);
		SkinningData skinningData = skinLoader.extractSkinData();

		SkeletonLoader jointsLoader = new SkeletonLoader(node.getChild("library_visual_scenes"),
				skinningData.jointOrder);
		SkeletonData jointsData = jointsLoader.extractBoneData();

		GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), skinningData.verticesSkinData);
		MeshData meshData = g.extractModelData();

		return new AnimatedModelData(meshData, jointsData);
	}

	public static AnimationData loadColladaAnimation(AdvancedFile colladaFile) {
		XmlNode node = XmlParser.loadXmlFile(colladaFile);
		XmlNode animNode = node.getChild("library_animations");
		XmlNode jointsNode = node.getChild("library_visual_scenes");
		AnimationLoader loader = new AnimationLoader(animNode, jointsNode);
		AnimationData animData = loader.extractAnimation();
		return animData;
	}

}
