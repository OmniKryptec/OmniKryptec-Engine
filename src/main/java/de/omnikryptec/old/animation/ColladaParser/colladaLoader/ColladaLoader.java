/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.old.animation.ColladaParser.colladaLoader;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.old.animation.ColladaParser.dataStructures.*;
import de.omnikryptec.old.util.Instance;
import de.omnikryptec.old.util.XMLUtil;
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
	final SkeletonLoader jointsLoader = new SkeletonLoader(XMLUtil.getChild(node, "library_visual_scenes"),
		skinningData.jointOrder);
	final SkeletonData jointsData = jointsLoader.extractBoneData();
	final GeometryLoader g = new GeometryLoader(XMLUtil.getChild(node, "library_geometries"),
		skinningData.verticesSkinData);
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
