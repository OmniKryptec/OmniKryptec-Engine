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

package de.omnikryptec.gameobject.particles;

import org.joml.Vector3f;

public class ParticleSpawnArea {
	
	public static enum ParticleSpawnAreaType{
		POINT, CIRCLE, SHPERE, LINE, DIRECTION;
	}
	
	private float data=0;
	private ParticleSpawnAreaType type;
	private final Vector3f direction = new Vector3f(0,1,0);
	
	public ParticleSpawnArea(ParticleSpawnAreaType type, float data){
		this.data = data;
		this.type = type;
	}
	
	public ParticleSpawnArea(ParticleSpawnAreaType type, Vector3f v, float length){
		this(type, v.x, v.y, v.z, length);
	}

	
	public ParticleSpawnArea(ParticleSpawnAreaType type, float x, float y, float z, float length){
		this.type = type;
		this.direction.set(x,y,z);
		this.data = length;
	}
	
	public Vector3f getDirection(){
		return direction;
	}
	
	public float getData(){
		return data;
	}
	
	public ParticleSpawnArea setData(float d){
		this.data = d;
		return this;
	}
	
	public ParticleSpawnAreaType getType(){
		return type;
	}
	
	public ParticleSpawnArea setType(ParticleSpawnAreaType type){
		this.type = type;
		return this;
	}
	
}
