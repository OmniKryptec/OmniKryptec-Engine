/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.old.shader.files.postprocessing;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.graphics.shader.base.uniform.UniformSampler;
import de.omnikryptec.graphics.shader.base.uniform.UniformVec3;
import de.omnikryptec.old.shader.base.Shader;

public class ColorSpaceShader extends Shader {

    public static final UniformSampler sampler = new UniformSampler("tex");
    public static final UniformVec3 value = new UniformVec3("levels");

    public ColorSpaceShader() {
	super(DEF_SHADER_LOC_PP_VS, new AdvancedFile(true, SHADER_LOCATION_PP, "color_space_shader_frag.glsl"),
		Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR, value, sampler);
	start();
	sampler.loadTexUnit(0);
    }

}
