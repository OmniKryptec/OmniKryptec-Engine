package omnikryptec.animation.ColladaParser.dataStructures;

import omnikryptec.loader.ResourceObject;

/**
 * Contains the extracted data for an animated model, which includes the mesh
 * data, and skeleton (joints heirarchy) data.
 *
 * @author Karl
 *
 */
public class AnimatedModelData implements ResourceObject {

    private final SkeletonData joints;
    private final MeshData mesh;

    public AnimatedModelData(MeshData mesh, SkeletonData joints) {
        this.joints = joints;
        this.mesh = mesh;
    }

    public SkeletonData getJointsData() {
        return joints;
    }

    public MeshData getMeshData() {
        return mesh;
    }

}
