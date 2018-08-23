package de.omnikryptec.gameobject.particlesV2;

public interface SimulatorPerParticle {

	void step(float dt, int particleIndex, ParticleSimulation simulation);
	
}
