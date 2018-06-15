package omnikryptec.gameobject.particlesV2;

import java.util.ArrayList;
import java.util.List;

public class GroupSimulator implements SimulatorPerSimulation{

	private List<SimulatorPerParticle> singleSims = new ArrayList<>();
	
	@Override
	public void step(float dt, ParticleSimulation sim) {
		for(int i=0; i<sim.size(); i++) {
			for(SimulatorPerParticle s : singleSims) {
				s.step(dt, i, sim);
			}
		}
	}
	
	public void addSingleSim(SimulatorPerParticle sim) {
		this.singleSims.add(sim);
	}

	@Override
	public boolean isOpenCLSimulation() {
		return false;
	}

}
