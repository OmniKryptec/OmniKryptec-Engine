package omnikryptec.gameobject.particlesV2;

import java.util.HashMap;
import java.util.Map;

public class ParticleSimulationBuffers {

	private Map<String, AttributeStorage> buffers = new HashMap<>();

	public AttributeStorage getAttributeStorage(String id) {
		return buffers.get(id);
	}
	
	public Map<String, AttributeStorage> getBuffers(){
		return buffers;
	}
	
	public void newsize(int s) {
		for(AttributeStorage as : buffers.values()) {
			as.getFlBuBuffer().changeSize(s*as.getAttribute().getComponentSize());
		}
	}
}
