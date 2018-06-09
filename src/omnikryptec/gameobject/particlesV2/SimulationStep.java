package omnikryptec.gameobject.particlesV2;

import java.util.Map;

public interface SimulationStep {

	void step(float dt, int pInfo, Map<String, ParticleSimulationAttribute> map);
	
}
