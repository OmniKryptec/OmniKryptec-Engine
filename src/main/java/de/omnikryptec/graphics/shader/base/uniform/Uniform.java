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

package de.omnikryptec.graphics.shader.base.uniform;

import org.lwjgl.opengl.GL20;

import de.omnikryptec.old.shader.base.Shader;
import de.omnikryptec.old.util.logger.LogLevel;
import de.omnikryptec.old.util.logger.Logger;

public abstract class Uniform {

	private static final int NOT_FOUND = -1;

	private String name;
	private int location;
	private boolean muted=false;
	private boolean isfound=false;
	
	protected Uniform(String name) {
		this(name, false);
	}
	
	protected Uniform(String name, boolean mute) {
		this.name = name;
	}

	protected void storeUniformLocation(Shader shader) {
		location = GL20.glGetUniformLocation(shader.getId(), name);
		if(location == NOT_FOUND) {
			isfound = false;
			if (!muted && Logger.isDebugMode()) {
				Logger.log(shader.getName() + ": No uniform variable called " + name + " found!", LogLevel.WARNING);
			}
		}else {
			isfound = true;
		}
	}

	protected int getLocation() {
		return location;
	}

	@Override
	public String toString() {
		return "Name: " + name + " Location: " + location;
	}

	public Uniform mute() {
		this.muted = true;
		return this;
	}
	
	public boolean isFound() {
		return isfound;
	}
	
}