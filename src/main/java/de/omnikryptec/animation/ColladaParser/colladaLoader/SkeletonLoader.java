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

package de.omnikryptec.animation.ColladaParser.colladaLoader;

import java.nio.FloatBuffer;
import java.util.List;

import org.jdom2.Element;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import de.omnikryptec.animation.ColladaParser.dataStructures.JointData;
import de.omnikryptec.animation.ColladaParser.dataStructures.SkeletonData;
import de.omnikryptec.util.XMLUtil;

/**
 * Loads the Skeleton
 *
 * @author Karl &amp; Panzer1119
 */
public class SkeletonLoader {

    private final Element armatureData;

    private final List<String> boneOrder;

    private int jointCount = 0;

    public SkeletonLoader(Element visualSceneNode, List<String> boneOrder) {
        this.armatureData = XMLUtil.getChildWithAttribute(XMLUtil.getChild(visualSceneNode, "visual_scene"), "node", "id", "Armature");
        this.boneOrder = boneOrder;
    }

    public final SkeletonData extractBoneData() {
        final Element headNode = XMLUtil.getChild(armatureData, "node");
        final JointData headJoint = loadJointData(headNode, true);
        return new SkeletonData(jointCount, headJoint);
    }

    private JointData loadJointData(Element jointNode, boolean isRoot) {
        final JointData joint = extractMainJointData(jointNode, isRoot);
        XMLUtil.getChildren(jointNode, "node").stream().forEach((childNode) -> {
            joint.addChild(loadJointData(childNode, false));
        });
        return joint;
    }

    private JointData extractMainJointData(Element jointNode, boolean isRoot) {
        final String nameId = jointNode.getAttributeValue("id");
        final int index = boneOrder.indexOf(nameId);
        final String[] matrixData = XMLUtil.getChild(jointNode, "matrix").getText().split(" ");
        final Matrix4f matrix = new Matrix4f();
        matrix.set(convertData(matrixData));
        matrix.transpose();
        if (isRoot) {
            // because in Blender z is up, but in our game y is up.
            ColladaLoader.CORRECTION.mul(matrix, matrix);
        }
        jointCount++;
        return new JointData(index, nameId, matrix);
    }

    private FloatBuffer convertData(String[] rawData) {
        final float[] matrixData = new float[16];
        for (int i = 0; i < matrixData.length; i++) {
            matrixData[i] = Float.parseFloat(rawData[i]);
        }
        final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        buffer.put(matrixData);
        buffer.flip();
        return buffer;
    }

}
