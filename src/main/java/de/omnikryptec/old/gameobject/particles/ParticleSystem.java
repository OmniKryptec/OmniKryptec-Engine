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

package de.omnikryptec.old.gameobject.particles;

import java.util.Random;

import org.joml.Vector3f;

import de.omnikryptec.old.gameobject.GameObject3D;
import de.omnikryptec.old.main.OmniKryptecEngine;
import de.omnikryptec.old.util.Maths;

public class ParticleSystem extends GameObject3D {

    /**
     * The particlesystem will emit the particles/sec amount of particles in one
     * tick and after that it will die. The system maybe can be resetted. Maybe not
     * supported by all ParticleSystems
     */
    public static final float LIFELENGTH_SYSTEM_ONETICKBURST = -3;

    /**
     * Maybe not supported by all ParticleSystems.
     */
    public static final float LIFELENGTH_NEVER_DIE = -2;
    /**
     * Maybe not supported by all ParticleSystems.
     */
    public static final float LIFELENGTH_DIE_ONLY_IN_ATTRACTOR = -1;

    protected float timemultiplier = 1;
    protected Random random = new Random();

    public ParticleSystem setTimeMultiplier(float f) {
	this.timemultiplier = f;
	return this;
    }

    public float getTimeMultiplier() {
	return timemultiplier;
    }

    protected float getScaledDeltatime() {
	return OmniKryptecEngine.instance().getDeltaTimef() * getTimeMultiplier();
    }

    protected Vector3f generateRandomUnitVectorWithinCone(Vector3f coneDirection, double coneangle) {
	return Maths.generateRandomUnitVectorWithinCone(random, coneDirection, coneangle);
    }

    protected Vector3f generateRandomUnitVector() {
	return Maths.generateRandomUnitVector(random);
    }

    protected float getErroredValue(float average, float errorMargin) {
	return Maths.getErroredValue(random, average, errorMargin);
    }
}
