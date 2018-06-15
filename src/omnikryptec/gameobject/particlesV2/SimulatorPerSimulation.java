package omnikryptec.gameobject.particlesV2;

public interface SimulatorPerSimulation {
	
	void step(float dt, ParticleSimulation sim);
	
	boolean isOpenCLSimulation();
}
