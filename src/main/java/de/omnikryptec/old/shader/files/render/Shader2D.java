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

package de.omnikryptec.old.shader.files.render;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.old.graphics.SpriteBatch;
import de.omnikryptec.old.shader.base.Shader;
import de.omnikryptec.old.shader.base.UniformMatrix;
import de.omnikryptec.old.shader.base.UniformSampler;

public class Shader2D extends Shader {

    private final UniformSampler sampler = new UniformSampler("sampler");
    private final UniformMatrix projview = new UniformMatrix("projview");

    public Shader2D() {
        super(new AdvancedFile(true, SHADER_LOCATION_RENDER, "2d_vert.glsl"),
                new AdvancedFile(true, SHADER_LOCATION_RENDER, "2d_frag.glsl"), "pos", "rgba", "uv");
        registerUniforms(sampler, projview);
        start();
        sampler.loadTexUnit(0);
    }

    @Override
    public void onDrawBatchStart(SpriteBatch batch) {
    	projview.loadMatrix(batch.getCamera().getProjectionViewMatrix());
    }

}
