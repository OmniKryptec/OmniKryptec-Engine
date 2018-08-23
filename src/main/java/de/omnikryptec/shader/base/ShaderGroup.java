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

package de.omnikryptec.shader.base;

import de.omnikryptec.util.exceptions.OmniKryptecException;

import java.util.HashMap;

public class ShaderGroup {
	
	private HashMap<Integer, Shader> shaders = new HashMap<>();
	
	
	public ShaderGroup(Shader lvl0) {
		addShader(0, lvl0);
	}
	
	public Shader getShaderForLvl(int lvl) {
		int start = lvl;
		Shader sh = shaders.get(lvl);
		while(sh==null) {
			lvl--;
			if(lvl<0) {
				throw new OmniKryptecException("No shader for lvl "+start+" found!");
			}
			sh = shaders.get(lvl);
		}
		return sh;
	}
	
	public ShaderGroup addShader(int lvl, Shader s) {
		if(lvl<0) {
			throw new OmniKryptecException("Level is too small: "+lvl);
		}
		shaders.put(lvl, s);
		return this;
	}
	
}
