/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.libapi.exposed.render;

import java.util.EnumMap;
import java.util.Map;

public class RenderState {

    public static enum BlendMode {
        ADDITIVE, ALPHA, MULTIPLICATIVE, OFF;
    }

    public static enum CullMode {
        BACK, FRONT, OFF;
    }

    public static enum DepthMode {
        LESS, EQUAL, GREATER, ALWAYS, NEVER;
    }

    public static enum RenderConfig {
        BLEND, DEPTH_TEST, CULL_FACES, MULTISAMPLE, WRITE_DEPTH, WRITE_COLOR;
    }

    public static enum PolyMode {
        FILL, LINE, POINT;
    }

    private BlendMode blendMode;
    private CullMode cullMode;
    private DepthMode depthMode;
    private PolyMode polyMode;
    private final Map<RenderConfig, Boolean> renderConfig = new EnumMap<>(RenderConfig.class);

    public boolean isEnable(final RenderConfig opt) {
        //Null-values should be false
        return this.renderConfig.get(opt);
    }

    public BlendMode getBlendMode() {
        return this.blendMode;
    }

    public CullMode getCullMode() {
        return this.cullMode;
    }

    public DepthMode getDepthMode() {
        return this.depthMode;
    }

    public PolyMode getPolyMode() {
        return this.polyMode;
    }

}
