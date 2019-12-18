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

public class RenderState implements Cloneable {

    public static RenderState of(final Object... objects) {
        final RenderState state = new RenderState();
        if (objects.length > 5) {
            throw new IllegalArgumentException("Illegal amount of arguments");
        }
        int bools = 0;
        for (final Object o : objects) {
            if (o == null) {
                throw new NullPointerException("array entry == null");
            }
            if (o instanceof BlendMode) {
                state.setBlendMode((BlendMode) o);
            } else if (o instanceof CullMode) {
                state.setCullMode((CullMode) o);
            } else if (o instanceof DepthMode) {
                state.setDepthMode((DepthMode) o);
            } else if (o instanceof Boolean) {
                bools++;
                if (bools == 1) {
                    state.setWriteColor((Boolean) o);
                } else if (bools == 2) {
                    state.setWriteDepth((Boolean) o);
                } else {
                    throw new IllegalArgumentException("too many booleans");
                }
            } else {
                throw new IllegalArgumentException("Invalid argument type");
            }
        }
        return state;
    }

    public static enum BlendMode {
        ADDITIVE, ALPHA, MULTIPLICATIVE, OFF;
    }

    public static enum CullMode {
        BACK, FRONT, OFF;
    }

    public static enum DepthMode {
        LESS, EQUAL, GREATER, ALWAYS, NEVER, OFF, DEFAULT;
    }

    private BlendMode blendMode = null;
    private CullMode cullMode = null;
    private DepthMode depthMode = null;
    private boolean writeColor, writeDepth;

    public RenderState() {
        setDefault();
    }

    public void setDefault() {
        this.blendMode = BlendMode.OFF;
        this.cullMode = CullMode.OFF;
        this.depthMode = DepthMode.OFF;
        this.writeColor = true;
        this.writeDepth = true;
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

    public boolean isWriteColor() {
        return this.writeColor;
    }

    public void setWriteColor(final boolean writeColor) {
        this.writeColor = writeColor;
    }

    public boolean isWriteDepth() {
        return this.writeDepth;
    }

    public void setWriteDepth(final boolean writeDepth) {
        this.writeDepth = writeDepth;
    }

    @Override
    public RenderState clone() {
        RenderState clone = null;
        try {
            clone = (RenderState) super.clone();
        } catch (final CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }

}
