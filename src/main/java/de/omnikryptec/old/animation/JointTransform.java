/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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
import org.joml.Vector3f;

import de.omnikryptec.old.util.Quaternion;

/**
 * JointTransform
 *
 * @author Panzer1119
 */
public class JointTransform {

    private final Vector3f position;
    private final Quaternion rotation;

    /**
     * Creates a JointTransform
     *
     * @param position Vector3f Position
     * @param rotation Quaternion Rotation
     */
    public JointTransform(Vector3f position, Quaternion rotation) {
	this.position = position;
	this.rotation = rotation;
    }

    protected final Matrix4f getLocalTransform() {
	final Matrix4f matrix = new Matrix4f();
	matrix.translate(position);
	matrix.mul(rotation.toRotationMatrix(), matrix);
	return matrix;
    }

    protected static final JointTransform interpolate(JointTransform frame_1, JointTransform frame_2,
	    float progression) {
	final Vector3f pos = interpolate(frame_1.position, frame_2.position, progression);
	final Quaternion rot = Quaternion.interpolate(frame_1.rotation, frame_2.rotation, progression);
	return new JointTransform(pos, rot);
    }

    private static final Vector3f interpolate(Vector3f start, Vector3f end, float progression) {
	final float x = start.x + (end.x - start.x) * progression;
	final float y = start.y + (end.y - start.y) * progression;
	final float z = start.z + (end.z - start.z) * progression;
	return new Vector3f(x, y, z);
    }

    @Override
    public int hashCode() {
	int hash = 1;
	hash = hash * 2 + position.hashCode();
	hash = hash * 3 + rotation.toRotationMatrix().hashCode();
	return hash;
    }

}
