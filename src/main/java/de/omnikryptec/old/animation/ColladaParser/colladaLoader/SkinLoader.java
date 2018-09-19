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

package de.omnikryptec.old.animation.ColladaParser.colladaLoader;

import de.omnikryptec.old.animation.ColladaParser.dataStructures.SkinningData;
import de.omnikryptec.old.animation.ColladaParser.dataStructures.VertexSkinData;
import de.omnikryptec.old.util.XMLUtil;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Loads a Skin
 *
 * @author Karl &amp; Panzer1119
 */
public class SkinLoader {

    private final Element skinningData;
    private final int maxWeights;

    public SkinLoader(Element controllersNode, int maxWeights) {
        this.skinningData = XMLUtil.getChild(XMLUtil.getChild(controllersNode, "controller"), "skin");
        this.maxWeights = maxWeights;
    }

    public final SkinningData extractSkinData() {
        final List<String> jointsList = loadJointsList();
        final float[] weights = loadWeights();
        final Element weightsDataNode = XMLUtil.getChild(skinningData, "vertex_weights");
        final int[] effectorJointCounts = getEffectiveJointsCounts(weightsDataNode);
        final List<VertexSkinData> vertexWeights = getSkinData(weightsDataNode, effectorJointCounts, weights);
        return new SkinningData(jointsList, vertexWeights);
    }

    private List<String> loadJointsList() {
        final Element inputNode = XMLUtil.getChild(skinningData, "vertex_weights");
        final String jointDataId = XMLUtil.getChildWithAttribute(inputNode, "input", "semantic", "JOINT").getAttributeValue("source").substring(1);
        final Element jointsNode = XMLUtil.getChild(XMLUtil.getChildWithAttribute(skinningData, "source", "id", jointDataId), "Name_array");
        final String[] names = jointsNode.getText().split(" ");
        List<String> jointsList = new ArrayList<>();
        jointsList.addAll(Arrays.asList(names));
        return jointsList;
    }

    private float[] loadWeights() {
        final Element inputNode = XMLUtil.getChild(skinningData, "vertex_weights");
        final String weightsDataId = XMLUtil.getChildWithAttribute(inputNode, "input", "semantic", "WEIGHT").getAttributeValue("source").substring(1);
        final Element weightsNode = XMLUtil.getChild(XMLUtil.getChildWithAttribute(skinningData, "source", "id", weightsDataId), "float_array");
        final String[] rawData = weightsNode.getText().split(" ");
        final float[] weights = new float[rawData.length];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = Float.parseFloat(rawData[i]);
        }
        return weights;
    }

    private int[] getEffectiveJointsCounts(Element weightsDataNode) {
        final String[] rawData = XMLUtil.getChild(weightsDataNode, "vcount").getText().split(" ");
        int[] counts = new int[rawData.length];
        for (int i = 0; i < rawData.length; i++) {
            counts[i] = Integer.parseInt(rawData[i]);
        }
        return counts;
    }

    private List<VertexSkinData> getSkinData(Element weightsDataNode, int[] counts, float[] weights) {
        final String[] rawData = XMLUtil.getChild(weightsDataNode, "v").getText().split(" ");
        final List<VertexSkinData> skinningData = new ArrayList<>();
        int pointer = 0;
        for (int count : counts) {
            VertexSkinData skinData = new VertexSkinData();
            for (int i = 0; i < count; i++) {
                int jointId = Integer.parseInt(rawData[pointer++]);
                int weightId = Integer.parseInt(rawData[pointer++]);
                skinData.addJointEffect(jointId, weights[weightId]);
            }
            skinData.limitJointNumber(maxWeights);
            skinningData.add(skinData);
        }
        return skinningData;
    }

}
