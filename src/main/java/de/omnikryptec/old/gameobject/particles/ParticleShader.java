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

package de.omnikryptec.old.gameobject.particles;

import de.omnikryptec.graphics.shader.base.uniform.UniformFloat;
import de.omnikryptec.graphics.shader.base.uniform.UniformMatrix;
import de.omnikryptec.graphics.shader.base.uniform.UniformVec4;
import de.omnikryptec.old.shader.base.Attribute;
import de.omnikryptec.old.shader.base.Shader;

public class ParticleShader extends Shader {

	private static final String VERTEX_FILE = "/de/omnikryptec/old/gameobject/particles/particle_shader_vert.glsl";
	private static final String FRAGMENT_FILE = "/de/omnikryptec/old/gameobject/particles/particle_shader_frag.glsl";

	public final UniformFloat nrOfRows = new UniformFloat("nrRows");
	public final UniformMatrix projMatrix = new UniformMatrix("projectionMatrix");
	public final UniformVec4 uvs = new UniformVec4("uvs");
	
	public ParticleShader() {
		super("ParticleShader", Shader.class.getResourceAsStream(VERTEX_FILE),
				Shader.class.getResourceAsStream(FRAGMENT_FILE), new Attribute("position", 0),
				new Attribute("modelViewMatrix", 1), new Attribute("texOffsets", 5), new Attribute("blendFac", 6), new Attribute("color", 7));
		registerUniforms(nrOfRows, projMatrix, uvs);

	}

}
