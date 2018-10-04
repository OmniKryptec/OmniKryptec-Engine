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

package de.omnikryptec.old.shader.base;

import java.util.HashMap;

import de.omnikryptec.old.util.logger.LogLevel;
import de.omnikryptec.old.util.logger.Logger;

public class ShaderPack {

	private HashMap<String, ShaderGroup> pack;
	protected ShaderGroup defaultShader;
	protected boolean defaultShaderIfError = true;

	public ShaderPack(ShaderGroup defaults) {
		this( new HashMap<>(), defaults);
	}

	public ShaderPack(HashMap<String, ShaderGroup> pack, ShaderGroup defaults) {
		this.pack = pack;
		this.defaultShader = defaults;
	}

	public ShaderPack addShader(String renderPassName, ShaderGroup shader) {
		if (renderPassName == null || renderPassName.length() == 0 || shader == null) {
			Logger.log("renderPassName or the shader is invalid!", LogLevel.WARNING);
			return this;
		}
		pack.put(renderPassName, shader);
		return this;
	}

	public ShaderPack removeShader(String name) {
		pack.remove(name);
		return this;
	}

	public Shader getShader(String renderPassName, int lvl) {
		if(renderPassName==null||renderPassName.isEmpty()) {
			return defaultShader.getShaderForLvl(lvl);
		}
		ShaderGroup sh = pack.get(renderPassName);
		if (sh == null && defaultShader != null && defaultShaderIfError) {
			sh = this.defaultShader;
		}
		return sh.getShaderForLvl(lvl);
	}

	public ShaderPack setDefaultShader(ShaderGroup s) {
		this.defaultShader = s;
		return this;
	}

	public Shader getDefaultShader() {
		return defaultShader.getShaderForLvl(0);
	}
	
	public ShaderGroup getDefaultShaderGroup() {
		return defaultShader;
	}
}
