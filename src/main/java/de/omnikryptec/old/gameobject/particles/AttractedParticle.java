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

import de.omnikryptec.old.resource.texture.ParticleAtlas;
import de.omnikryptec.old.util.EnumCollection.RenderType;
import de.omnikryptec.old.util.PhysicsUtil;
import org.joml.Vector3f;

import java.util.ArrayList;

/**
 * AttractedParticle
 * 
 * @author Panzer1119 &amp; pcfreak9000
 */
public class AttractedParticle extends Particle {

    protected Vector3f positionLast = new Vector3f();
    protected boolean attractedByParticles = false;
    protected Vector3f velocity;
    protected float lifeLength;
    protected float elapsedTime = 0;
    protected float mass = 1.0F;
    private final AttractedPaticleSystem system;
    private boolean living = true;
    private float startscale = 1, endscale = 1;
    private float[] color1 = new float[] { 1, 1, 1, 1 };
    private float[] color2 = new float[] { 1, 1, 1, 1 };

    public AttractedParticle(ParticleAtlas tex, Vector3f pos, Vector3f vel, float mass, float lifeLength, float rot,
	    float scale, AttractedPaticleSystem system, RenderType type, float[] startcolor, float[] endcolor) {
	this(tex, pos, vel, mass, lifeLength, rot, scale, scale, system, type, startcolor, endcolor);
    }

    public AttractedParticle(ParticleAtlas tex, Vector3f pos, Vector3f vel, float mass, float lifeLength, float rot,
	    float startscale, float endscale, AttractedPaticleSystem system, RenderType type, float[] startcolor,
	    float[] endcolor) {
	super(pos, tex, type);
	positionLast.set(pos);
	setRotation(rot);
	this.velocity = vel;
	this.mass = mass;
	this.lifeLength = lifeLength;
	this.elapsedTime = 0;
	this.system = system;
	this.startscale = startscale;
	this.endscale = endscale;
	this.color1 = startcolor;
	this.color2 = endcolor;
	color.setFrom(startcolor);
	setScale(startscale);
	wantsupdatelast = true;
	wantsmultithreaded = attractedByParticles;
    }

    public AttractedPaticleSystem getSystem() {
	return system;
    }

    @Override
    protected float getLifeFactor() {
	return lifeLength <= -1 ? -1 : elapsedTime / lifeLength;
    }

    public boolean isAttractedByParticles() {
	return attractedByParticles;
    }

    public AttractedParticle setAttractedByParticles(boolean attractedByParticles) {
	this.wantsmultithreaded = attractedByParticles;
	this.attractedByParticles = attractedByParticles;
	return this;
    }

    private float timemultiplier, lf;
    private Vector3f changeable = new Vector3f(), att;
    private boolean allowMovementThisFrame = false;

    private int disallowing_mov = 0;
    private ArrayList<ParticleAttractor> disabler;

    @Override
    protected boolean update() {
	timemultiplier = system.getScaledDeltatime();
	elapsedTime += timemultiplier;
	if (disabler != null) {
	    for (ParticleAttractor atr : system.getAttractorData()) {
		if (!atr.isEnabled() && disabler.contains(atr)) {
		    disallowing_mov--;
		    disabler.remove(atr);
		    if (disabler.isEmpty()) {
			disabler = null;
			break;
		    }
		}
	    }
	}
	if (disallowing_mov <= 0) {
	    final Vector3f acceleration = new Vector3f(0, 0, 0);
	    final Vector3f direction = new Vector3f(0, 0, 0);
	    boolean b = false;
	    float ddd = 0, attenu = 1;
	    outer: for (ParticleAttractor attractorData : system.getAttractorData()) {
		if (!attractorData.isEnabled()) {
		    continue;
		}
		changeable = attractorData.getAbsolutePos();
		allowMovementThisFrame = true;
		if (!attractorData.isInfinite()) {
		    direction.set(changeable).sub(position);
		    b = true;
		} else {
		    direction.set(changeable);
		    b = false;
		}
		if ((direction.lengthSquared() <= (attractorData.getTolerance() * attractorData.getTolerance()))) {
		    switch (attractorData.getMode()) {
		    case KILL_ON_REACH:
			living = false;
			break outer;
		    case NOTHING:
			break;
		    case STOP_FOREVER_ON_REACH:
			disallowing_mov++;
			velocity.set(0);
			break outer;
		    case STOP_ON_REACH:
			allowMovementThisFrame = false;
			velocity.set(0);
			break outer;
		    case STOP_UNTIL_DISABLED_ON_REACH:
			disallowing_mov++;
			velocity.set(0);
			if (disabler == null) {
			    disabler = new ArrayList<>(1);
			}
			disabler.add(attractorData);
			break outer;
		    default:
			break;
		    }
		}
		if (attractorData.getMode() == AttractorMode.NOTHING || allowMovementThisFrame) {
		    if (b) {
			att = attractorData.getAttenuation();
			if (att.y != 0 || att.z != 0) {
			    ddd = direction.length();
			}
			attenu = att.x + att.y * ddd + att.z * ddd * ddd;
			attenu = 1 / attenu;
		    }
		    direction.normalize();
		    direction.mul(attractorData.getGravitation() / mass * attenu);
		    acceleration.add(direction);
		}
	    }
	    if (attractedByParticles) {
		float force = 0;
		for (AttractedParticle particle : system.particles) {
		    if (!particle.attractedByParticles) {
			continue;
		    }
		    particle.positionLast.sub(position, changeable);
		    final float radiusSquared = changeable.lengthSquared();
		    if (radiusSquared == 0) {
			continue;
		    }
		    force = (PhysicsUtil.GRAVITATIONAL_CONSTANT * (mass * particle.mass)) / radiusSquared;
		    changeable.normalize();
		    changeable.mul(force);
		    acceleration.add(changeable);
		}
	    }
	    if (disallowing_mov <= 0 && elapsedTime != lifeLength) {
		velocity.add(acceleration.mul(timemultiplier, changeable));
		position.add(velocity.mul(timemultiplier, changeable));
	    }
	}
	lf = getLifeFactor();
	if (lf <= -1) {
	    setScale(startscale);
	    color.set(color1[0], color1[1], color1[2], color1[3]);
	} else {
	    setScale((endscale - startscale) * lf + startscale);
	    color.setR((color2[0] - color1[0]) * lf + color1[0]);
	    color.setG((color2[1] - color1[1]) * lf + color1[1]);
	    color.setB((color2[2] - color1[2]) * lf + color1[2]);
	    color.setA((color2[3] - color1[3]) * lf + color1[3]);
	}
	final boolean livesNextFrame = (lifeLength == -2 || (lifeLength == -1 && living)
		|| (elapsedTime < lifeLength && living));
	if (!livesNextFrame) {
	    system.particles.remove(this);
	}
	return livesNextFrame;
    }

    @Override
    protected boolean updateLast() {
	positionLast.set(position);
	return true;
    }

}
