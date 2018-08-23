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

package de.omnikryptec.gameobject.particlesV2;

import de.omnikryptec.util.PhysicsUtil;

public class NBodySim implements SimulatorPerSimulation {

	private float myG;
	private float eps;

	public NBodySim() {
		this(0.000001f);
	}

	public NBodySim(float eps) {
		this(PhysicsUtil.GRAVITATIONAL_CONSTANT, eps);
	}

	public NBodySim(float g, float eps) {
		this.myG = g;
		this.eps = eps;
	}

	@Override
	public void step(float dt, ParticleSimulation sim) {
		AttributeStorage position = sim.getParticleData().getAttributeStorage(ParticleAttribute.POSITION);
		AttributeStorage velocity = sim.getParticleData().getAttributeStorage(ParticleAttribute.VELOCITY);
		AttributeStorage mass = sim.getParticleData().getAttributeStorage(ParticleAttribute.MASS);
		float dx, dy, dz, len;
		for (int i = 0; i < sim.size(); i++) {
			for (int j = 0; j < sim.size(); j++) {
				dx = position.get(j, 0) - position.get(i, 0);
				dy = position.get(j, 1) - position.get(i, 1);
				dz = position.get(j, 2) - position.get(i, 2);
				len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz + eps);
				len = len * len * len;
				velocity.add(i, 0, myG * mass.get(j) * dx * dt);
				velocity.add(i, 1, myG * mass.get(j) * dy * dt);
				velocity.add(i, 2, myG * mass.get(j) * dz * dt);
			}
		}
	}

	@Override
	public boolean isOpenCLSimulation() {
		return false;
	}

	// @Override
	// public void step(float dt, int particles, Map<String,
	// ParticleSimulationAttribute> map) {
	// ParticleSimulationAttribute position = map.get(SimulationFactory.POSITION);
	// ParticleSimulationAttribute acceleration =
	// map.get(SimulationFactory.ACCELERATION);
	// ParticleSimulationAttribute mass = map.get(SimulationFactory.MASS);
	// float dx, dy, dz, len;
	// for (int i = 0; i < particles; i++) {
	// for (int j = 0; j < particles; j++) {
	// dx = position.get(j, 0) - position.get(i, 0);
	// dy = position.get(j, 1) - position.get(i, 1);
	// dz = position.get(j, 2) - position.get(i, 2);
	// len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz + eps);
	// len = len * len * len;
	// acceleration.add(i, 0, myG * mass.get(j) * dx);
	// acceleration.add(i, 1, myG * mass.get(j) * dy);
	// acceleration.add(i, 2, myG * mass.get(j) * dz);
	// }
	// }
	// }

}
