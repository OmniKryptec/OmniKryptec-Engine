/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.animation;

import de.omnikryptec.old.util.Instance;
import de.omnikryptec.old.util.Maths;
import org.joml.Matrix4f;

import java.util.HashMap;

/**
 * Animator
 *
 * @author Panzer1119
 */
public class Animator {

    private final AnimatedModel animatedModel;

    private Animation currentAnimation;
    private float animationTime = 0;
    private boolean loop = true;
    private float speedFactor = 1.0F;

    /**
     * Creates an Animator
     *
     * @param animatedModel AnimatedModel Model to be animated
     */
    public Animator(AnimatedModel animatedModel) {
	this(animatedModel, true);
    }

    /**
     * Creates an Animator
     *
     * @param animatedModel AnimatedModel Model to be animated
     * @param loop          Boolean Set if the animation should get looped
     */
    public Animator(AnimatedModel animatedModel, boolean loop) {
	this.animatedModel = animatedModel;
	this.loop = loop;
    }

    /**
     * Carrys out an animation
     *
     * @param animation Animation Animation
     * @return A reference to this Animator
     */
    public final Animator doAnimation(Animation animation) {
	return doAnimation(animation, 0);
    }

    /**
     * Carrys out an animation
     *
     * @param animation     Animation Animation
     * @param animationTime Float Time
     * @return A reference to this Animator
     */
    public final Animator doAnimation(Animation animation, float animationTime) {
	return doAnimation(animation, animationTime, loop);
    }

    /**
     * Carrys out an animation
     *
     * @param animation     Animation Animation
     * @param animationTime Float Time
     * @param loop          Boolean Set if the animation should get looped
     * @return A reference to this Animator
     */
    public final Animator doAnimation(Animation animation, float animationTime, boolean loop) {
	this.currentAnimation = animation;
	this.animationTime = animationTime;
	this.loop = loop;
	return this;
    }

    /**
     * Updates this Animator
     *
     * @return A reference to this Animator
     */
    public final Animator update() {
	if (currentAnimation == null || (isAnimationOver() && !loop)) {
	    return this;
	}
	increaseAnimationTime();
	HashMap<String, Matrix4f> currentPose = calculateCurrentAnimationPose();
	applyPoseToJoints(currentPose, animatedModel.getRootJoint(), new Matrix4f());
	return this;
    }

    /**
     * Deletes this Animator
     *
     * @return A reference to this Animator
     */
    public final Animator delete() {
	this.currentAnimation = null;
	this.loop = false;
	return this;
    }

    /**
     * Resets the Animation
     *
     * @param loop Boolean Set if the animation should get looped
     * @return A reference to this Animator
     */
    public final Animator reset(boolean loop) {
	this.loop = loop;
	animationTime = 0;
	return this;
    }

    /**
     * Returns the speed
     *
     * @return Float Speed
     */
    public final float getSpeedFactor() {
	return speedFactor;
    }

    /**
     * Sets the speed
     *
     * @param speedFactor Float Speed
     * @return A reference to this Animator
     */
    public final Animator setSpeedFactor(float speedFactor) {
	this.speedFactor = Maths.clamp(speedFactor, -10.0F, 10.0F);
	return this;
    }

    private final Animator increaseAnimationTime() {
	animationTime += (Instance.getDeltaTimef() * speedFactor);
	if (isAnimationOver() && loop) {
	    while (animationTime < 0) {
		animationTime += currentAnimation.getLengthInSeconds();
	    }
	    animationTime %= currentAnimation.getLengthInSeconds();
	}
	return this;
    }

    protected final boolean isAnimationOver() {
	if (currentAnimation == null) {
	    return true;
	}
	return animationTime > currentAnimation.getLengthInSeconds() || animationTime < 0;
    }

    private final HashMap<String, Matrix4f> calculateCurrentAnimationPose() {
	final KeyFrame[] frames = getPreviousAndNextFrames();
	final float progression = calculateProgression(frames[0], frames[1]);
	return interpolatePoses(frames[0], frames[1], progression);
    }

    private final Animator applyPoseToJoints(HashMap<String, Matrix4f> currentPose, Joint joint,
	    Matrix4f parentTransform) {
	final Matrix4f currentLocalTransform = currentPose.get(joint.getName());
	final Matrix4f currentTransform = new Matrix4f();
	parentTransform.mul(currentLocalTransform, currentTransform);
	for (Joint child : joint.getChildren()) {
	    applyPoseToJoints(currentPose, child, currentTransform);
	}
	currentTransform.mul(joint.getInverseBindTransform(), currentTransform);
	joint.setAnimationTransform(currentTransform);
	return this;
    }

    private final KeyFrame[] getPreviousAndNextFrames() {
	final KeyFrame[] allFrames = currentAnimation.getKeyFrames();
	KeyFrame previousFrame = allFrames[0];
	KeyFrame nextFrame = allFrames[0];
	for (int i = 1; i < allFrames.length; i++) {
	    nextFrame = allFrames[i];
	    if (nextFrame.getTimestamp() > animationTime) {
		break;
	    }
	    previousFrame = allFrames[i];
	}
	return new KeyFrame[] { previousFrame, nextFrame };
    }

    private final float calculateProgression(KeyFrame previousFrame, KeyFrame nextFrame) {
	final float totalTime = nextFrame.getTimestamp() - previousFrame.getTimestamp();
	final float currentTime = animationTime - previousFrame.getTimestamp();
	return currentTime / totalTime;
    }

    private final HashMap<String, Matrix4f> interpolatePoses(KeyFrame previousFrame, KeyFrame nextFrame,
	    float progression) {
	final HashMap<String, Matrix4f> currentPose = new HashMap<>();
	for (String name : previousFrame.getJointKeyFrames().keySet()) {
	    final JointTransform previousTransform = previousFrame.getJointKeyFrames().get(name);
	    final JointTransform nextTransform = nextFrame.getJointKeyFrames().get(name);
	    final JointTransform currentTransform = JointTransform.interpolate(previousTransform, nextTransform,
		    progression);
	    currentPose.put(name, currentTransform.getLocalTransform());
	}
	return currentPose;
    }

    public final Animation getAnimation() {
	return currentAnimation;
    }

    public final Animator setLoop(boolean loop) {
	this.loop = loop;
	return this;
    }

    public final Animator setAnimationTime(float animationTime) {
	this.animationTime = animationTime;
	return this;
    }

    public final float getAnimationTime() {
	return animationTime;
    }

    public final boolean isLoop() {
	return loop;
    }

}
