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

package de.omnikryptec.old.physics;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.MotionState;
import de.omnikryptec.old.gameobject.Entity;
import de.omnikryptec.old.gameobject.GameObject3D;
import de.omnikryptec.old.resource.model.Model;
import de.omnikryptec.old.resource.model.TexturedModel;
import de.omnikryptec.old.util.PhysicsUtil;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 *
 * @author Panzer1119
 */
public class RigidBodyBuilder {

    private float mass = 0.0F;
    private MotionState motionState = null;
    private CollisionShape collisionShape = null;
    private Vector3f inertia = new Vector3f(0, 0, 0);
    private RigidBodyConstructionInfo rigidBodyConstructionInfo = null;

    public RigidBodyBuilder() {
    }

    public RigidBodyBuilder(float mass) {
	this.mass = mass;
    }

    public RigidBodyBuilder(MotionState motionState) {
	this.motionState = motionState;
    }

    public RigidBodyBuilder(org.joml.Vector3f position, org.joml.Quaternionf rotation) {
	setDefaultMotionState(position, rotation);
    }

    public RigidBodyBuilder(Vector3f position, Quat4f rotation) {
	setDefaultMotionState(position, rotation);
    }

    public RigidBodyBuilder(Entity entity) {
	loadFromEntity(entity);
    }

    public RigidBodyBuilder(TexturedModel texturedModel) {
	loadCollisionShape(texturedModel);
    }

    public RigidBodyBuilder(Model model) {
	loadCollisionShape(model);
    }

    public RigidBodyBuilder(CollisionShape collisionShape) {
	this.collisionShape = collisionShape;
    }

    public RigidBodyBuilder(float mass, MotionState motionState, CollisionShape collisionShape) {
	this.mass = mass;
	this.motionState = motionState;
	this.collisionShape = collisionShape;
    }

    public RigidBodyBuilder(float mass, Vector3f position, Quat4f rotation, CollisionShape collisionShape) {
	this.mass = mass;
	setDefaultMotionState(position, rotation);
	this.collisionShape = collisionShape;
    }

    public RigidBodyBuilder(float mass, org.joml.Vector3f position, org.joml.Quaternionf rotation,
	    CollisionShape collisionShape) {
	this.mass = mass;
	setDefaultMotionState(position, rotation);
	this.collisionShape = collisionShape;
    }

    public final float getMass() {
	return mass;
    }

    public final RigidBodyBuilder setMass(float mass) {
	this.mass = mass;
	rigidBodyConstructionInfo = null;
	return this;
    }

    public final Vector3f getInertia() {
	return inertia;
    }

    public final RigidBodyBuilder setInertia(Vector3f inertia) {
	this.inertia = inertia;
	rigidBodyConstructionInfo = null;
	return this;
    }

    public final MotionState getMotionState() {
	return motionState;
    }

    public final RigidBodyBuilder setMotionState(MotionState motionState) {
	this.motionState = motionState;
	rigidBodyConstructionInfo = null;
	return this;
    }

    public final RigidBodyBuilder setDefaultMotionState(GameObject3D gameObject) {
	if (gameObject == null) {
	    return this;
	}
	setDefaultMotionState(gameObject.getTransform().getPosition(), gameObject.getTransform().getRotation());
	return this;
    }

    public final RigidBodyBuilder setDefaultMotionState(Vector3f position, Quat4f rotation) {
	this.motionState = PhysicsUtil.createDefaultMotionStateOfPosition(position, rotation);
	rigidBodyConstructionInfo = null;
	return this;
    }

    public final RigidBodyBuilder setDefaultMotionState(org.joml.Vector3f position, org.joml.Quaternionf rotation) {
	this.motionState = PhysicsUtil.createDefaultMotionStateOfPosition(position, rotation);
	rigidBodyConstructionInfo = null;
	return this;
    }

    public final CollisionShape getCollisionShape() {
	return collisionShape;
    }

    public final RigidBodyBuilder setCollisionShape(CollisionShape collisionShape) {
	this.collisionShape = collisionShape;
	return this;
    }

    public final RigidBodyBuilder loadFromEntity(Entity entity) {
	if (entity == null) {
	    return this;
	}
	loadCollisionShape((TexturedModel) entity.getAdvancedModel());
	setDefaultMotionState(entity);
	return this;
    }

    public final RigidBodyBuilder loadCollisionShape(TexturedModel texturedModel) {
	if (texturedModel == null) {
	    return this;
	}
	return loadCollisionShape(texturedModel.getModel());
    }

    public final RigidBodyBuilder loadCollisionShape(Model model) {
	this.collisionShape = PhysicsUtil.createConvexHullShape(model);
	rigidBodyConstructionInfo = null;
	return this;
    }

    private RigidBodyConstructionInfo createRigidBodyConstructionInfo() {
	return new RigidBodyConstructionInfo(mass, motionState, collisionShape, inertia);
    }

    public final RigidBodyConstructionInfo getRigidBodyConstructionInfo() {
	if (rigidBodyConstructionInfo == null) {
	    rigidBodyConstructionInfo = createRigidBodyConstructionInfo();
	}
	return rigidBodyConstructionInfo;
    }

    public final RigidBodyBuilder setRigidBodyConstructionInfo(RigidBodyConstructionInfo rigidBodyConstructionInfo) {
	this.rigidBodyConstructionInfo = rigidBodyConstructionInfo;
	return this;
    }

    public final RigidBodyBuilder updateRigidBodyConstructionInfo() {
	rigidBodyConstructionInfo = createRigidBodyConstructionInfo();
	return this;
    }

    public final RigidBody create() {
	return new RigidBody(getRigidBodyConstructionInfo());
    }

}
