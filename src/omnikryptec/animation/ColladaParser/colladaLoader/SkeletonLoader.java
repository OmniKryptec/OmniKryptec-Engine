package omnikryptec.animation.ColladaParser.colladaLoader;

import java.nio.FloatBuffer;
import java.util.List;

import org.jdom2.Element;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

import omnikryptec.animation.ColladaParser.dataStructures.JointData;
import omnikryptec.animation.ColladaParser.dataStructures.SkeletonData;
import omnikryptec.util.XMLUtil;

/**
 * Loads the Skeleton
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
        matrix.load(convertData(matrixData));
        matrix.transpose();
        if (isRoot) {
            // because in Blender z is up, but in our game y is up.
            Matrix4f.mul(ColladaLoader.CORRECTION, matrix, matrix);
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
