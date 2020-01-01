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

package de.omnikryptec.old.gameobject.particles;

import de.omnikryptec.old.gameobject.GameObject;
import de.omnikryptec.old.gameobject.GameObject3D;
import org.joml.Vector3f;

public class ParticleAttractor {

    protected float gravitation = 0, tolerance = 0;
    protected boolean infinite = false;
    protected GameObject3D positionable;
    protected AttractorMode mode = AttractorMode.NOTHING;
    protected boolean enabled = true;
    protected Vector3f attenuation = new Vector3f(1, 0, 0);

    public ParticleAttractor(GameObject3D p) {
	this.positionable = p;
    }

    public ParticleAttractor(float x, float y, float z) {
	this.positionable = new GameObject3D();
	positionable.getTransform().setPosition(x, y, z);
    }

    public float getGravitation() {
	return gravitation;
    }

    public ParticleAttractor setGravitation(float gravitation) {
	this.gravitation = gravitation;
	return this;
    }

    public float getTolerance() {
	return tolerance;
    }

    public ParticleAttractor setDistanceTolerance(float tolerance) {
	this.tolerance = tolerance;
	return this;
    }

    public AttractorMode getMode() {
	return this.mode;
    }

    public ParticleAttractor setMode(AttractorMode mode) {
	this.mode = mode;
	return this;
    }

    public boolean isInfinite() {
	return infinite;
    }

    public ParticleAttractor setInfinite(boolean infinite) {
	this.infinite = infinite;
	return this;
    }

    public GameObject getAttractor() {
	return positionable;
    }

    public ParticleAttractor setAttractor(GameObject3D go) {
	this.positionable = go;
	return this;
    }

    public Vector3f getAbsolutePos() {
	return positionable.getTransform().getPosition();
    }

    public ParticleAttractor setEnabled(boolean b) {
	this.enabled = b;
	return this;
    }

    public boolean isEnabled() {
	return this.enabled;
    }

    public ParticleAttractor setAttenuation(float a, float b, float c) {
	attenuation.set(a, b, c);
	return this;
    }

    public Vector3f getAttenuation() {
	return attenuation;
    }

}
