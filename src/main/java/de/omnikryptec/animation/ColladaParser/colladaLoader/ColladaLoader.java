package de.omnikryptec.animation.ColladaParser.colladaLoader;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.animation.ColladaParser.dataStructures.*;
import de.omnikryptec.util.Instance;
import de.omnikryptec.util.XMLUtil;
import org.jdom2.Document;
import org.jdom2.Element;
import org.joml.Matrix4f;

/**
 * Loads a model from a collada XML file.
 *
 * @author Karl &amp; Panzer1119
 */
public class ColladaLoader {

    public static final Matrix4f CORRECTION = new Matrix4f().rotate((float) Math.toRadians(-90), Instance.MATHS_X_AXIS);

    public static final MeshData getMeshDataOnly(AdvancedFile colladaFile) {
        return loadColladaModel("", colladaFile, 0).getMeshData();
    }

    public static final AnimatedModelData loadColladaModel(AdvancedFile colladaFile, int maxWeights) {
        return loadColladaModel("", colladaFile, maxWeights);
    }

    public static final AnimatedModelData loadColladaModel(String name, AdvancedFile colladaFile, int maxWeights) {
        final Document document = XMLUtil.getDocument(colladaFile.createInputStream());
        if (document == null) {
            return null;
        }
        final Element node = document.getRootElement();
        final SkinLoader skinLoader = new SkinLoader(XMLUtil.getChild(node, "library_controllers"), maxWeights);
        final SkinningData skinningData = skinLoader.extractSkinData();
        final SkeletonLoader jointsLoader = new SkeletonLoader(XMLUtil.getChild(node, "library_visual_scenes"), skinningData.jointOrder);
        final SkeletonData jointsData = jointsLoader.extractBoneData();
        final GeometryLoader g = new GeometryLoader(XMLUtil.getChild(node, "library_geometries"), skinningData.verticesSkinData);
        final MeshData meshData = g.extractModelData();
        return new AnimatedModelData(name, meshData, jointsData);
    }

    public static final AnimationData loadColladaAnimation(AdvancedFile colladaFile) {
        final Document document = XMLUtil.getDocument(colladaFile.createInputStream());
        if (document == null) {
            return null;
        }
        final Element node = document.getRootElement();
        final Element animNode = XMLUtil.getChild(node, "library_animations");
        final Element jointsNode = XMLUtil.getChild(node, "library_visual_scenes");
        final AnimationLoader loader = new AnimationLoader(animNode, jointsNode);
        final AnimationData animData = loader.extractAnimation();
        return animData;
    }

}
