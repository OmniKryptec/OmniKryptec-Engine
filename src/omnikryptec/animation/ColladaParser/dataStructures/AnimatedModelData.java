package omnikryptec.animation.ColladaParser.dataStructures;

import omnikryptec.resource.loader.ResourceObject;

/**
 * Contains the extracted data for an animated model, which includes the mesh
 * data, and skeleton (joints heirarchy) data.
 *
 * @author Karl
 *
 */
public class AnimatedModelData implements ResourceObject {

    private final String name;
    private final SkeletonData joints;
    private final MeshData mesh;

    public AnimatedModelData(String name, MeshData mesh, SkeletonData joints) {
        this.name = name;
        this.joints = joints;
        this.mesh = mesh;
    }

    public SkeletonData getJointsData() {
        return joints;
    }

    public MeshData getMeshData() {
        return mesh;
    }

    @Override
    public String getName() {
        return name;
    }

	@Override
	public ResourceObject delete() {
		return this;
	}


}
