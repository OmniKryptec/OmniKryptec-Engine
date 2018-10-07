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

package de.omnikryptec.old.shader.files.postprocessing;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.graphics.shader.base.UniformSampler;
import de.omnikryptec.graphics.shader.base.UniformVec2;
import de.omnikryptec.old.shader.base.Shader;

public class CombineShader extends Shader {

    public final UniformSampler sampler1 = new UniformSampler("tex1");
    public final UniformSampler sampler2 = new UniformSampler("tex2");
    public final UniformVec2 weights = new UniformVec2("weights");

    public CombineShader() {
        super(DEF_SHADER_LOC_PP_VS,
                new AdvancedFile(true, SHADER_LOCATION_PP, "combine_shader_frag.glsl"),
                Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR);
        registerUniforms(sampler1, sampler2, weights);
        start();
        sampler1.loadTexUnit(0);
        sampler2.loadTexUnit(1);
    }

}
