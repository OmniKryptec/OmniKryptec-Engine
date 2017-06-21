package omnikryptec.animation;

import omnikryptec.model.Model;
import omnikryptec.texture.SimpleTexture;
import org.lwjgl.util.vector.Matrix4f;

/**
 * AnimatedModel
 * @author Panzer1119
 */
public class AnimatedModel {
    
    private final Model model;
    private final SimpleTexture texture;
    
    private final Joint rootJoint;
    private final int jointCount;
    
    private final Animator animator;
    
    /**
     * Creates an animated model
     * @param model Model Model
     * @param texture SimpleTexture Texture
     * @param rootJoint Joint Root Joint
     * @param jointCount Integer Joint count
     */
    public AnimatedModel(Model model, SimpleTexture texture, Joint rootJoint, int jointCount) {
        this.model = model;
        this.texture = texture;
        this.rootJoint = rootJoint;
        this.jointCount = jointCount;
        this.animator = new Animator(this);
        rootJoint.calculateInverseBindTransform(new Matrix4f());
    }
    
    /**
     * Returns the model
     * @return Model Model
     */
    public final Model getModel() {
        return model;
    }
    
    /**
     * Returns the texture
     * @return SimpleTexture Texture
     */
    public final SimpleTexture getTexture() {
        return texture;
    }
    
    /**
     * Returns the root joint
     * @return Joint Root Joint
     */
    public final Joint getRootJoint() {
        return rootJoint;
    }
    
    /**
     * Deletes this AnimatedModel
     * @return A reference to this AnimatedModel
     */
    public final AnimatedModel delete() {
        model.getVao().delete();
        texture.delete();
        return this;
    }
    
    /**
     * Carrys out an animation
     * @param animation Animation Animation
     * @return A reference to this AnimatedModel
     */
    public final AnimatedModel doAnimation(Animation animation) {
        animator.doAnimation(animation);
        return this; 
    }
    
    /**
     * Updates this AnimatedModel
     * @return A reference to this AnimatedModel
     */
    public final AnimatedModel update() {
        animator.update();
        return this;
    }
    
    /**
     * Returns all important model-space transformations of the Joints
     * @return Matrix4f Array Transformation of the Joints
     */
    public final Matrix4f[] getJointTransforms() {
        final Matrix4f[] jointMatrices = new Matrix4f[jointCount];
        addJointsToArray(rootJoint, jointMatrices);
        return jointMatrices;
    }
    
    private final AnimatedModel addJointsToArray(Joint headJoint, Matrix4f[] jointMatrices) {
        jointMatrices[headJoint.getIndex()] = headJoint.getAnimatedTransform();
        headJoint.getChildren().stream().forEach((child) -> {
            addJointsToArray(child, jointMatrices);
        });
        return this;
    }
    
}
