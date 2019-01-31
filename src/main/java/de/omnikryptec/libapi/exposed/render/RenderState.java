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

public class RenderState implements Cloneable {

    public static enum BlendMode {
        ADDITIVE, ALPHA, MULTIPLICATIVE;
    }

    public static enum CullMode {
        BACK, FRONT;
    }

    public static enum DepthMode {
        LESS, EQUAL, GREATER, ALWAYS, NEVER;
    }

    public static enum RenderConfig {
        BLEND, DEPTH_TEST, CULL_FACES, WRITE_DEPTH, WRITE_COLOR;
    }

    public static enum PolyMode {
        FILL, LINE, POINT;
    }

    private BlendMode blendMode = null;
    private CullMode cullMode = null;
    private DepthMode depthMode = null;
    private PolyMode polyMode = null;
    private final Map<RenderConfig, Boolean> renderConfig = new EnumMap<>(RenderConfig.class);

    public boolean isEnable(final RenderConfig opt) {
        final Boolean bool = this.renderConfig.get(opt);
        return bool != null ? bool : false;
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

    public RenderState setBlendMode(final BlendMode blendMode) {
        this.blendMode = blendMode;
        return this;
    }

    public RenderState setCullMode(final CullMode cullMode) {
        this.cullMode = cullMode;
        return this;
    }

    public RenderState setDepthMode(final DepthMode depthMode) {
        this.depthMode = depthMode;
        return this;
    }

    public RenderState setPolyMode(final PolyMode polyMode) {
        this.polyMode = polyMode;
        return this;
    }

    public RenderState setRenderConfig(final RenderConfig renderConfig, final boolean enable) {
        this.renderConfig.put(renderConfig, enable);
        return this;
    }

    @Override
    public RenderState clone() {
        RenderState clone = null;
        try {
            clone = (RenderState) super.clone();
            clone.renderConfig.clear();
            clone.renderConfig.putAll(this.renderConfig);
        } catch (final CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }

}
