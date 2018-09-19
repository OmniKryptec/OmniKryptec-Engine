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
import de.omnikryptec.old.shader.base.Shader;
import de.omnikryptec.old.shader.base.UniformSampler;
import de.omnikryptec.old.shader.base.UniformVec4;

public class FilterShader extends Shader {

    public final UniformVec4 channels = new UniformVec4("channels");
    public final UniformSampler sampler = new UniformSampler("tex");
    public final UniformSampler extra = new UniformSampler("extra");

    public FilterShader() {
        super(DEF_SHADER_LOC_PP_VS,
                new AdvancedFile(true, SHADER_LOCATION_PP, "extrainfo_reader_frag.glsl"),
                Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR);
        registerUniforms(sampler, extra, channels);
        start();
        sampler.loadTexUnit(0);
        extra.loadTexUnit(1);
    }

}
