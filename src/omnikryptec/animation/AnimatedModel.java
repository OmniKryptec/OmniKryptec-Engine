package omnikryptec.animation;

import java.util.ArrayList;

import org.lwjgl.util.vector.Matrix4f;

import omnikryptec.model.AdvancedModel;
import omnikryptec.model.Material;
import omnikryptec.model.Model;
import omnikryptec.texture.Texture;

/**
 * AnimatedModel
 * @author Panzer1119
 */
public class AnimatedModel implements AdvancedModel {
    
    private static final ArrayList<AnimatedModel> animatedModels = new ArrayList<>();
    
    private final Model model;
    private final Texture texture;
    private final Material material;
    
    private final Joint rootJoint;
    private final int jointCount;
    
    private final Animator animator;
    
    /**
     * Creates an animated model
     * @param model Model Model
     * @param texture Texture Texture
     * @param rootJoint Joint Root Joint
     * @param jointCount Integer Joint count
     */
    public AnimatedModel(Model model, Texture texture, Joint rootJoint, int jointCount) {
        this(model, texture, rootJoint, jointCount, new Material());
    }
    
    /**
     * Creates an animated model with a custom material
     * @param model Model Model
     * @param texture Texture Texture
     * @param rootJoint Joint Root Joint
     * @param jointCount Integer Joint count
     * @param material Material Material
     */
    public AnimatedModel(Model model, Texture texture, Joint rootJoint, int jointCount, Material material) {
        this.model = model;
        this.texture = texture;
        this.material = material;
        this.rootJoint = rootJoint;
        this.jointCount = jointCount;
        this.animator = new Animator(this);
        rootJoint.calculateInverseBindTransform(new Matrix4f());
        animatedModels.add(this);
    }
    
    /**
     * Returns the model
     * @return Model Model
     */
    @Override
    public final Model getModel() {
        return model;
    }
    
    /**
     * Returns the texture
     * @return Texture Texture
     */
    @Override
    public final Texture getTexture() {
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
        //texture.delete();
        animator.delete();
        animatedModels.remove(this);
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
    
    /**
     * Returns the Animator
     * @return Animator Animator
     */
    public final Animator getAnimator() {
        return animator;
    }

    @Override
    public Material getMaterial() {
        return material;
    }
    
    /**
     * Updates all created AnimatedModels
     */
    public static final void updateAllAnimatedModels() {
        animatedModels.stream().forEach((animatedModel) -> {
            animatedModel.update();
        });
    }
    
}
