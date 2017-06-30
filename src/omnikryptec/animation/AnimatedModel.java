package omnikryptec.animation;

import java.util.ArrayList;
import java.util.HashMap;

import omnikryptec.resource.loader.ResourceLoader;
import omnikryptec.resource.model.AdvancedModel;
import omnikryptec.resource.model.Material;
import omnikryptec.resource.model.Model;
import omnikryptec.resource.texture.Texture;
import omnikryptec.test.saving.DataMap;

import org.joml.Matrix4f;

/**
 * AnimatedModel
 *
 * @author Panzer1119
 */
public class AnimatedModel implements AdvancedModel {

    public static final HashMap<String, ArrayList<AnimatedModel>> animatedModels = new HashMap<>();

    private String name;
    private final Model model;
    private final Texture texture;
    private final Material material;

    private final Joint rootJoint;
    private final int jointCount;

    private final Animator animator;
    
    public AnimatedModel() {
        this.name = null;
        this.model = null;
        this.texture = null;
        this.material = null;
        this.rootJoint = null;
        this.jointCount = -1;
        this.animator = null;
    }

    /**
     * Creates an animated model
     *
     * @param model Model Model
     * @param texture Texture Texture
     * @param rootJoint Joint Root Joint
     * @param jointCount Integer Joint count
     */
    public AnimatedModel(String name, Model model, Texture texture, Joint rootJoint, int jointCount) {
        this(name, model, texture, rootJoint, jointCount, new Material());
    }

    /**
     * Creates an animated model with a custom material
     *
     * @param model Model Model
     * @param texture Texture Texture
     * @param rootJoint Joint Root Joint
     * @param jointCount Integer Joint count
     * @param material Material Material
     */
    public AnimatedModel(String name, Model model, Texture texture, Joint rootJoint, int jointCount, Material material) {
        this.name = name;
        this.model = model;
        this.texture = texture;
        this.material = material;
        this.rootJoint = rootJoint;
        this.jointCount = jointCount;
        this.animator = new Animator(this);
        rootJoint.calculateInverseBindTransform(new Matrix4f());
        ArrayList<AnimatedModel> ams = animatedModels.get(name);
        if(ams == null) {
            ams = new ArrayList<>();
            animatedModels.put(name, ams);
        }
        ams.add(this);
    }

    /**
     * Returns the model
     *
     * @return Model Model
     */
    @Override
    public final Model getModel() {
        return model;
    }

    /**
     * Returns the texture
     *
     * @return Texture Texture
     */
    @Override
    public final Texture getTexture() {
        return texture;
    }

    /**
     * Returns the root joint
     *
     * @return Joint Root Joint
     */
    public final Joint getRootJoint() {
        return rootJoint;
    }

    /**
     * Deletes this AnimatedModel
     *
     * @return A reference to this AnimatedModel
     */
    @Override
    public final AnimatedModel delete() {
        model.getVao().delete();
        //texture.delete();
        animator.delete();
        deleteAll(true);
        return this;
    }
    
    @SuppressWarnings("unchecked")
	private final AnimatedModel deleteAll(boolean all) {
        ArrayList<AnimatedModel> ams = (ArrayList<AnimatedModel>) animatedModels.get(name).clone();
        if(ams != null) {
            ams.remove(this);
            if(ams.isEmpty()) {
                animatedModels.remove(name);
            } else if(all) {
                ams.stream().forEach((am) ->  {
                    am.deleteAll(false);
                });
            }
        }
        return this;
    }
    
    /**
     * Carrys out an animation
     *
     * @param animation Animation Animation
     * @return A reference to this AnimatedModel
     */
    public final AnimatedModel doAnimation(Animation animation) {
        animator.doAnimation(animation);
        return this;
    }

    /**
     * Carrys out an animation
     *
     * @param animation Animation Animation
     * @param animationTime Float Time
     * @return A reference to this AnimatedModel
     */
    public final AnimatedModel doAnimation(Animation animation, float animationTime) {
        animator.doAnimation(animation, animationTime);
        return this;
    }
    
    /**
     * Carrys out an animation
     *
     * @param animation Animation Animation
     * @param animationTime Float Time
     * @param loop Boolean Set if the animation should get looped
     * @return A reference to this AnimatedModel
     */
    public final AnimatedModel doAnimation(Animation animation, float animationTime, boolean loop) {
        animator.doAnimation(animation, animationTime, loop);
        return this;
    }

    /**
     * Updates this AnimatedModel
     *
     * @return A reference to this AnimatedModel
     */
    public final AnimatedModel update() {
        animator.update();
        return this;
    }

    /**
     * Returns all important model-space transformations of the Joints
     *
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
     *
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
        animatedModels.values().stream().forEach((ams) -> {
            ams.stream().forEach((am) -> {
               am.update();
            });
        });
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public final AnimatedModel copy() {
        return new AnimatedModel(name, model, texture, rootJoint, jointCount, material);
    }
    
    public static final AnimatedModel byName(String name) {
        final ArrayList<AnimatedModel> ams = animatedModels.get(name);
        if(ams != null && !ams.isEmpty()) {
            return ams.get(0).copy();
        }
        return null;
    }
    
    public final Animation getAnimation() {
        return animator.getAnimation();
    }

    @Override
    public DataMap toDataMap(DataMap data) {
        final Animation animation = getAnimation();
        if(animation != null) {
            data.put("animation", animation.getName());
        }
        data.put("speedFactor", animator.getSpeedFactor());
        data.put("animationTime", animator.getAnimationTime());
        data.put("loop", animator.isLoop());
        return data;
    }

    @Override
    public AnimatedModel fromDataMap(DataMap data) {
        final String name_animation = data.getString("animation");
        final float speedFactor = data.getFloat("speedFactor");
        final float animationTime = data.getFloat("animationTime");
        final boolean loop = data.getBoolean("loop");
        animator.setSpeedFactor(speedFactor);
        animator.setAnimationTime(animationTime);
        animator.setLoop(loop);
        if(name_animation != null) {
            doAnimation(ResourceLoader.getInstance().getData(Animation.class, name_animation), animationTime, loop);
        }
        return this;
    }
    
}
