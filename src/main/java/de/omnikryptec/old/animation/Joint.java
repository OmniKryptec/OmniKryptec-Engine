/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

import org.joml.Matrix4f;

import java.util.ArrayList;

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
     * @param index              Integer Index of the Joint
     * @param name               String Name of the Joint
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
	for (Joint child : children) {
	    child.calculateInverseBindTransform(bindTransform);
	}
	return this;
    }

}
