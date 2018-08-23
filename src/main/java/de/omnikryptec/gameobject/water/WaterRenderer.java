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

package de.omnikryptec.gameobject.water;

import de.omnikryptec.gameobject.Entity;
import de.omnikryptec.main.AbstractScene3D;
import de.omnikryptec.renderer.d3.RenderMap;
import de.omnikryptec.renderer.d3.Renderer;
import de.omnikryptec.renderer.d3.RendererRegistration;
import de.omnikryptec.resource.model.AdvancedModel;
import de.omnikryptec.shader.base.Shader;
import de.omnikryptec.shader.base.ShaderGroup;
import de.omnikryptec.shader.base.ShaderPack;
import de.omnikryptec.util.FrustrumFilter;
import de.omnikryptec.util.Level;
import de.omnikryptec.util.Priority;

import java.util.List;

/**
 * WaterRenderer
 *
 * @author Panzer1119
 */
@Priority(value = 1)
@Level(value = 2)
public class WaterRenderer extends Renderer {

    public WaterRenderer() {
        super(new ShaderPack(new ShaderGroup(new WaterShader())));
        RendererRegistration.register(this);
    }

    private List<Entity> stapel;
    private long vertcount = 0;

    @Override
    protected long render(AbstractScene3D s, RenderMap<AdvancedModel, List<Entity>> entities, Shader started, FrustrumFilter filter) {
        //TODO Fill it out
        return 0;
    }
}
