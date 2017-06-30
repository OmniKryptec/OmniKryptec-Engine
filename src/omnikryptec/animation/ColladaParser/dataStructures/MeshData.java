package omnikryptec.animation.ColladaParser.dataStructures;

import omnikryptec.resource.objConverter.ModelData;

/**
 * This object contains all the mesh data for an animated model that is to be
 * loaded into the VAO.
 *
 * @author Karl
 *
 */
public class MeshData extends ModelData {

    private int[] jointIds;
    private float[] vertexWeights;

    public MeshData(float[] vertices, float[] textureCoords, float[] normals, int[] indices, int[] jointIds, float[] vertexWeights) {
        super(vertices, textureCoords, normals, normals, indices, 0);
        this.jointIds = jointIds;
        this.vertexWeights = vertexWeights;
    }

    public MeshData(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, int[] indices, int[] jointIds, float[] vertexWeights) {
        super(vertices, textureCoords, normals, tangents, indices, 0);
        this.jointIds = jointIds;
        this.vertexWeights = vertexWeights;
    }

    public int[] getJointIds() {
        return jointIds;
    }

    public float[] getVertexWeights() {
        return vertexWeights;
    }

}
