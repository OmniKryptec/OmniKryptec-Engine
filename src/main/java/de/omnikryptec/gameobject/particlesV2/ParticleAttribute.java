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

public class ParticleAttribute {
	
	/**
	 * if PGC use -1 to remove the particle from all buffers
	 */
	public static final String LIFESTATUS = "lifestatus";
	
	public static final String MASS ="mass";
	
	public static final String POSITION="position";
	
	public static final String VELOCITY ="velocity";
	
	public static final String ACCELERATION ="acceleration";
	
	private String name;
	private int componentsize;
	
	public ParticleAttribute(String name, int componentsize) {
		this.name = name;
		this.componentsize = componentsize;
	}
	
	public String getName() {
		return name;
	}
	
	public int getComponentSize() {
		return componentsize;
	}
	
	public int calcIndex(int pIndex, int comp) {
		return pIndex*componentsize+comp;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
