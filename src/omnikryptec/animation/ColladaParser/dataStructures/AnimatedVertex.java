package omnikryptec.animation.ColladaParser.dataStructures;

import org.joml.Vector3f;

import omnikryptec.resource.objConverter.Vertex;

public class AnimatedVertex extends Vertex {

    private final VertexSkinData weightsData;

    public AnimatedVertex(int index, Vector3f position, VertexSkinData weightsData) {
        super(index, position);
        this.weightsData = weightsData;
    }

    public VertexSkinData getWeightsData() {
        return weightsData;
    }

}
