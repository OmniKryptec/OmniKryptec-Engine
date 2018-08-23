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
	
	public void resize(int s) {
		for(AttributeStorage as : buffers.values()) {
			as.getParticleBuffer().resize(s*as.getAttribute().getComponentSize());
		}
	}

	public void enableOpenCLMode(boolean b) {
		for(AttributeStorage as : buffers.values()) {
			as.getParticleBuffer().readyCL(b);
		}
	}
}
