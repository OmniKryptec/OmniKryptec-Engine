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
import de.omnikryptec.graphics.shader.base.UniformFloat;
import de.omnikryptec.graphics.shader.base.UniformMatrix;
import de.omnikryptec.graphics.shader.base.UniformSampler;
import de.omnikryptec.graphics.shader.base.UniformVec2;
import de.omnikryptec.graphics.shader.base.UniformVec3;
import de.omnikryptec.graphics.shader.base.UniformVec4;
import de.omnikryptec.old.shader.base.Shader;

public class FogShader extends Shader {

    public final UniformSampler depth = new UniformSampler("depth");
    public final UniformSampler texture = new UniformSampler("tex");
    public final UniformVec2 pixsize = new UniformVec2("pixelSize");
    public final UniformVec4 fog = new UniformVec4("fog");

    public final UniformFloat density = new UniformFloat("density");
    public final UniformFloat gradient = new UniformFloat("gradient");

    public final UniformMatrix invprojv = new UniformMatrix("invprojv");

    public final UniformVec3 campos = new UniformVec3("campos");

    public FogShader() {
        super(DEF_SHADER_LOC_PP_VS,
                new AdvancedFile(true, SHADER_LOCATION_PP, "fog_shader_frag.glsl"),
                Shader.DEFAULT_PP_VERTEX_SHADER_POS_ATTR);
        registerUniforms(depth, texture, pixsize, fog, density, gradient, invprojv, campos);
        start();
        texture.loadTexUnit(0);
        depth.loadTexUnit(1);
    }

}
