package omnikryptec.animation;

import java.util.ArrayList;
import org.joml.Matrix4f;

/**
 * Joint
 *
 * @author Panzer1119
 */
public class Joint {

    private final int index;
    private final String name;
    private final ArrayList<Joint> children = new ArrayList<>();

    private Matrix4f animatedTransform = new Matrix4f();

    private final Matrix4f localBindTransform;
    private final Matrix4f inverseBindTransform = new Matrix4f();

    /**
     * Creates a Joint used for the skeleton model
     *
     * @param index Integer Index of the Joint
     * @param name String Name of the Joint
     * @param localBindTransform Matrix4f Original Local Transformation Matrix
     */
    public Joint(int index, String name, Matrix4f localBindTransform) {
        this.index = index;
        this.name = name;
        this.localBindTransform = localBindTransform;
    }

    /**
     * Returns the index of this Joint
     *
     * @return Integer Index
     */
    public final int getIndex() {
        return index;
    }

    /**
     * Returns the name of this Joint
     *
     * @return String Name
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the children of this Joint
     *
     * @return ArrayList Joint Children
     */
    public final ArrayList<Joint> getChildren() {
        return children;
    }

    /**
     * Adds a child to this Joint
     *
     * @param child Joint Child Joint
     * @return A reference to this Joint
     */
    public final Joint addChild(Joint child) {
        this.children.add(child);
        return this;
    }

    /**
     * Returns the animated transformation matrix to be loaded up to the shader
     *
     * @return Matrix4f Animated Transformation Matrix
     */
    public final Matrix4f getAnimatedTransform() {
        return animatedTransform;
    }

    /**
     * Sets the animated transformation matrix
     *
     * @param animationTransform Matrix4f Animated Transformation Matrix
     * @return A reference to this Joint
     */
    public final Joint setAnimationTransform(Matrix4f animationTransform) {
        this.animatedTransform = animationTransform;
        return this;
    }

    /**
     * Returns the inverted local bind transformation matrix
     *
     * @return Matrix4f Inverse of the Bind Transformation Matrix
     */
    public final Matrix4f getInverseBindTransform() {
        return inverseBindTransform;
    }

    /**
     * Calculates the inverse of the bind transformation matrix during setup
     *
     * @param parentBindTransform Matrix4f Parent Bind Transform
     * @return A reference to this Joint
     */
    protected Joint calculateInverseBindTransform(Matrix4f parentBindTransform) {
        final Matrix4f bindTransform = new Matrix4f();
        parentBindTransform.mul(localBindTransform, bindTransform);
        bindTransform.invert(inverseBindTransform);
        children.stream().forEach((child) -> {
            child.calculateInverseBindTransform(bindTransform);
        });
        return this;
    }

}
