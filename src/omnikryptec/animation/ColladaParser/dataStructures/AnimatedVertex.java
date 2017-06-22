package omnikryptec.animation.ColladaParser.dataStructures;

import omnikryptec.objConverter.Vertex;

import org.lwjgl.util.vector.Vector3f;

public class AnimatedVertex extends Vertex {
	
    private final VertexSkinData weightsData;

    public AnimatedVertex(int index, Vector3f position, VertexSkinData weightsData){
        super(index, position);
        this.weightsData = weightsData;
    }

    public VertexSkinData getWeightsData(){
        return weightsData;
    }

}
