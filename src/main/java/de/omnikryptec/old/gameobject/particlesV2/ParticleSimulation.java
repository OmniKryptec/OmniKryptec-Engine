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

package de.omnikryptec.old.gameobject.particlesV2;

import java.util.ArrayList;
import java.util.List;

public class ParticleSimulation {

    private ParticleSimulationBuffers buffers = new ParticleSimulationBuffers();
    private List<SimulatorPerSimulation> sims = new ArrayList<>();

    private int max;
    private int size;

    public ParticleSimulation() {
	this(-1);
    }

    public ParticleSimulation(int max) {
	this.max = max;
    }

    public void simulate(float dt) {
	for (SimulatorPerSimulation s : sims) {
	    if (s.isOpenCLSimulation()) {
		buffers.enableOpenCLMode(true);
	    }
	    s.step(dt, this);
	    if (s.isOpenCLSimulation()) {
		buffers.enableOpenCLMode(false);
	    }
	}
    }

    public void addSimulator(SimulatorPerSimulation sim) {
	sims.add(sim);
    }

    public int size() {
	return size;
    }

    public int limit() {
	return max;
    }

    public boolean isLimited() {
	return max > 0;
    }

    public ParticleSimulationBuffers getParticleData() {
	return buffers;
    }

}
