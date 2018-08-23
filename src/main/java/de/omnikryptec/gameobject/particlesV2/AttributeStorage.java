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

public class AttributeStorage {
	
	private ParticleFloatBuffer buffer;
	private ParticleAttribute attribute;
	
	public AttributeStorage(ParticleAttribute att, int initialSize) {
		this.attribute = att;
		this.buffer = new ParticleFloatBuffer(attribute.getComponentSize()*initialSize);
	}
	
	public ParticleAttribute getAttribute() {
		return attribute;
	}
	
	public ParticleFloatBuffer getParticleBuffer() {
		return buffer;
	}
	
	public void set(int particleIndex, int comp, float data) {
		buffer.getWriteBuffer().put(attribute.calcIndex(particleIndex, comp), data);
	}
	
	public void add(int particleIndex, int comp, float data) {
		set(particleIndex, comp, data+get(particleIndex, comp));
	}
	
	public float get(int particleIndex, int comp) {
		return buffer.getReadBuffer().get(attribute.calcIndex(particleIndex, comp));
	}
	
	public float get(int index) {
		return buffer.getReadBuffer().get(index);
	}
	
}
