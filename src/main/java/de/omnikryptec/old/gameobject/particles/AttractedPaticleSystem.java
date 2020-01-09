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

import de.omnikryptec.old.gameobject.GameObject3D;
import de.omnikryptec.old.resource.texture.ParticleAtlas;
import de.omnikryptec.old.util.EnumCollection.RenderType;
import org.joml.Vector3f;

import java.util.LinkedList;

/**
 * AttractedPaticleSystem
 * 
 * @author Panzer1119 &amp; pcfreak9000
 */
public class AttractedPaticleSystem extends SimpleParticleSystem {

    protected final LinkedList<ParticleAttractor> attractorData = new LinkedList<>();
    protected final LinkedList<AttractedParticle> particles = new LinkedList<>();
    protected float averageMass = 1.0F;
    protected float massError = 0.0F;
    protected boolean particlesAttractingEachOther = false;

    public AttractedPaticleSystem(Vector3f pos, ParticleAtlas tex, float pps, float startspeed, float lifeLength,
	    RenderType type) {
	this(pos, tex, pps, startspeed, lifeLength, 1, type);
    }

    public AttractedPaticleSystem(Vector3f pos, ParticleAtlas tex, float pps, float startspeed, float lifeLength,
	    float scale, RenderType type) {
	this(pos, tex, pps, startspeed, lifeLength, scale, scale, type);
    }

    public AttractedPaticleSystem(Vector3f pos, ParticleAtlas tex, float pps, float startspeed, float lifeLength,
	    float scale, float endscale, RenderType type) {
	this(pos.x, pos.y, pos.z, tex, pps, startspeed, lifeLength, scale, endscale, type);
    }

    public AttractedPaticleSystem(float x, float y, float z, ParticleAtlas tex, float pps, float startspeed,
	    float lifeLength, RenderType type) {
	this(x, y, z, tex, pps, startspeed, lifeLength, 1, type);
    }

    public AttractedPaticleSystem(float x, float y, float z, ParticleAtlas tex, float pps, float startspeed,
	    float lifeLength, float scale, RenderType type) {
	this(x, y, z, tex, pps, startspeed, lifeLength, scale, scale, type);
    }

    public AttractedPaticleSystem(float x, float y, float z, ParticleAtlas tex, float pps, float startspeed,
	    float lifeLength, float scale, float endscale, RenderType type) {
	this.type = type;
	this.particlepersec = pps;
	this.averageSpeed = startspeed;
	this.averageLifeLength = lifeLength;
	this.averageStartScale = scale;
	this.averageEndScale = endscale;
	this.particletexture = tex;
	getTransform().setPosition(x, y, z);
    }

    public AttractedPaticleSystem addAttractor(float x, float y, float z, float a, float t) {
	return addAttractor(x, y, z, a, t, AttractorMode.NOTHING);
    }

    public AttractedPaticleSystem addAttractor(float x, float y, float z, float a, float t, AttractorMode d) {
	return addAttractor(x, y, z, a, t, d, false);
    }

    public AttractedPaticleSystem addAttractor(Vector3f pos, float a, float t) {
	return addAttractor(pos, a, t, AttractorMode.NOTHING);
    }

    public AttractedPaticleSystem addAttractor(Vector3f pos, float a, float t, AttractorMode d) {
	return addAttractor(pos.x, pos.y, pos.z, a, t, d, false);
    }

    public AttractedPaticleSystem addAttractor(GameObject3D go, float a, float t) {
	return addAttractor(go, a, t, AttractorMode.NOTHING);
    }

    public AttractedPaticleSystem addAttractor(GameObject3D go, float a, float t, AttractorMode d) {
	return addAttractor(go, a, t, d, false);
    }

    public AttractedPaticleSystem addAttractor(float x, float y, float z, float g, float t, AttractorMode mode,
	    boolean i) {
	return addAttractor(
		new ParticleAttractor(x, y, z).setGravitation(g).setDistanceTolerance(t).setMode(mode).setInfinite(i));
    }

    public AttractedPaticleSystem addAttractor(GameObject3D go, float g, float t, AttractorMode mode, boolean i) {
	return addAttractor(
		new ParticleAttractor(go).setGravitation(g).setDistanceTolerance(t).setMode(mode).setInfinite(i));
    }

    public AttractedPaticleSystem addAttractor(ParticleAttractor atr) {
	this.attractorData.add(atr);
	return this;
    }

    public ParticleAttractor getLastAddedAttractor() {
	return attractorData.getLast();
    }

    public LinkedList<ParticleAttractor> getAttractorData() {
	return attractorData;
    }

    public float getAverageMass() {
	return averageMass;
    }

    public AttractedPaticleSystem setAverageMass(float averageMass) {
	this.averageMass = averageMass;
	return this;
    }

    public float getMassError() {
	return massError;
    }

    public AttractedPaticleSystem setMassError(float massError) {
	this.massError = massError;
	return this;
    }

    public boolean isParticlesAttractingEachOther() {
	return particlesAttractingEachOther;
    }

    public AttractedPaticleSystem setParticlesAttractingEachOther(boolean particlesAttractingEachOther) {
	this.particlesAttractingEachOther = particlesAttractingEachOther;
	return this;
    }

    private Vector3f velocity;
    private float scale, lifeLength, endscale, mass;

    @Override
    protected Particle emitParticle(Vector3f center) {
	if (direction != null) {
	    velocity = generateRandomUnitVectorWithinCone(direction, directionAngel);
	} else {
	    velocity = generateRandomUnitVector();
	}
	velocity.mul(getErroredValue(averageSpeed, speedError));
	scale = getErroredValue(averageStartScale, startScaleError);
	if (averageStartScale == averageEndScale) {
	    endscale = scale;
	} else {
	    endscale = getErroredValue(averageEndScale, endScaleError);
	}
	mass = getErroredValue(averageMass, massError);
	lifeLength = averageLifeLength <= -1 ? averageLifeLength : getErroredValue(averageLifeLength, lifeError);
	final AttractedParticle particle = new AttractedParticle(particletexture, calcNewSpawnPos(center), velocity,
		mass, lifeLength, generateRotation(), scale, endscale, this, type, startcolor.getArray(),
		endcolor.getArray());
	particles.add(particle);
	particle.setAttractedByParticles(particlesAttractingEachOther);
	return particle;
    }

}
