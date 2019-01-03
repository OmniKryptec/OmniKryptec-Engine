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

import de.omnikryptec.old.animation.ColladaParser.dataStructures.AnimationData;
import de.omnikryptec.old.animation.ColladaParser.dataStructures.JointTransformData;
import de.omnikryptec.old.animation.ColladaParser.dataStructures.KeyFrameData;
import de.omnikryptec.old.util.XMLUtil;
import org.jdom2.Element;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.List;

/**
 * Loads an animation for a model from a collada XML file.
 * 
 * @author Karl &amp; Panzer1119
 */
public class AnimationLoader {

    private final Element animationData;
    private final Element jointHierarchy;

    public AnimationLoader(Element animationData, Element jointHierarchy) {
	this.animationData = animationData;
	this.jointHierarchy = jointHierarchy;
    }

    public final AnimationData extractAnimation() {
	final String rootNode = findRootJointName();
	final float[] times = getKeyTimes();
	final float duration = times[times.length - 1];
	final KeyFrameData[] keyFrames = initKeyFrames(times);
	final List<Element> animationNodes = XMLUtil.getChildren(animationData, "animation");
	animationNodes.stream().forEach((jointNode) -> {
	    loadJointTransforms(keyFrames, jointNode, rootNode);
	});
	return new AnimationData(duration, keyFrames);
    }

    private float[] getKeyTimes() {
	final Element timeData = XMLUtil
		.getChild(XMLUtil.getChild(XMLUtil.getChild(animationData, "animation"), "source"), "float_array");
	final String[] rawTimes = timeData.getText().split(" ");
	final float[] times = new float[rawTimes.length];
	for (int i = 0; i < times.length; i++) {
	    times[i] = Float.parseFloat(rawTimes[i]);
	}
	return times;
    }

    private KeyFrameData[] initKeyFrames(float[] times) {
	final KeyFrameData[] frames = new KeyFrameData[times.length];
	for (int i = 0; i < frames.length; i++) {
	    frames[i] = new KeyFrameData(times[i]);
	}
	return frames;
    }

    private void loadJointTransforms(KeyFrameData[] frames, Element jointData, String rootNodeId) {
	final String jointNameId = getJointName(jointData);
	final String dataId = getDataId(jointData);
	final Element transformData = XMLUtil.getChildWithAttribute(jointData, "source", "id", dataId);
	final String[] rawData = XMLUtil.getChild(transformData, "float_array").getText().split(" ");
	processTransforms(jointNameId, rawData, frames, jointNameId.equals(rootNodeId));
    }

    private String getDataId(Element jointData) {
	final Element node = XMLUtil.getChildWithAttribute(XMLUtil.getChild(jointData, "sampler"), "input", "semantic",
		"OUTPUT");
	return node.getAttributeValue("source").substring(1);
    }

    private String getJointName(Element jointData) {
	final Element channelNode = XMLUtil.getChild(jointData, "channel");
	final String data = channelNode.getAttributeValue("target");
	return data.split("/")[0];
    }

    private void processTransforms(String jointName, String[] rawData, KeyFrameData[] keyFrames, boolean root) {
	final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
	final float[] matrixData = new float[16];
	for (int i = 0; i < keyFrames.length; i++) {
	    for (int j = 0; j < 16; j++) {
		matrixData[j] = Float.parseFloat(rawData[i * 16 + j]);
	    }
	    buffer.clear();
	    buffer.put(matrixData);
	    buffer.flip();
	    final Matrix4f transform = new Matrix4f();
	    transform.set(buffer);
	    transform.transpose();
	    if (root) {
		// because up axis in Blender is different to up axis in game
		ColladaLoader.CORRECTION.mul(transform, transform);
	    }
	    keyFrames[i].addJointTransform(new JointTransformData(jointName, transform));
	}
    }

    private String findRootJointName() {
	final Element skeleton = XMLUtil.getChildWithAttribute(XMLUtil.getChild(jointHierarchy, "visual_scene"), "node",
		"id", "Armature");
	return XMLUtil.getChild(skeleton, "node").getAttributeValue("id");
    }

}
