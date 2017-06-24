package omnikryptec.animation.ColladaParser.colladaLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import omnikryptec.animation.ColladaParser.dataStructures.SkinningData;
import omnikryptec.animation.ColladaParser.dataStructures.VertexSkinData;
import omnikryptec.util.XMLUtil;
import org.jdom2.Element;

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
