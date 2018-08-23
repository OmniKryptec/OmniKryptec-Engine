package de.omnikryptec.gameobject.particlesV2;

import omnikryptec.util.PhysicsUtil;

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
