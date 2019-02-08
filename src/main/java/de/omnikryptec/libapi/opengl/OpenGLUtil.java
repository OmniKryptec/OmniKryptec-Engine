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

package de.omnikryptec.libapi.opengl;

import java.util.EnumMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

import de.omnikryptec.libapi.exposed.render.FBTarget.TextureFormat;
import de.omnikryptec.libapi.exposed.render.RenderAPI.BufferUsage;
import de.omnikryptec.libapi.exposed.render.RenderAPI.SurfaceBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.RenderState.BlendMode;
import de.omnikryptec.libapi.exposed.render.RenderState.CullMode;
import de.omnikryptec.libapi.exposed.render.RenderState.DepthMode;
import de.omnikryptec.libapi.exposed.render.RenderState.PolyMode;
import de.omnikryptec.libapi.exposed.render.RenderState.RenderConfig;
import de.omnikryptec.resource.MeshData.Primitive;
import de.omnikryptec.resource.parser.shader.ShaderParser.ShaderType;
import de.omnikryptec.util.data.Color;

public class OpenGLUtil {

    public static int typeId(final Type t) {
        switch (t) {
        case FLOAT:
            return GL11.GL_FLOAT;
        default:
            throw new IllegalArgumentException(t + "");
        }
    }

    public static int sizeof(final Type t) {
        switch (t) {
        case FLOAT:
            return 4;
        default:
            throw new IllegalArgumentException(t + "");
        }
    }

    public static int typeId(final ShaderType shaderType) {
        switch (shaderType) {
        case Compute:
            return GL43.GL_COMPUTE_SHADER;
        case Fragment:
            return GL20.GL_FRAGMENT_SHADER;
        case Geometry:
            return GL32.GL_GEOMETRY_SHADER;
        case TessellationControl:
            return GL40.GL_TESS_CONTROL_SHADER;
        case TessellationEvaluation:
            return GL40.GL_TESS_EVALUATION_SHADER;
        case Vertex:
            return GL20.GL_VERTEX_SHADER;
        default:
            throw new IllegalArgumentException(shaderType + "");

        }
    }

    public static int typeId(final Primitive primitiveType) {
        if (primitiveType == null) {
            //Assuming triangles because mostly triangles are used
            return GL11.GL_TRIANGLES;
        }
        switch (primitiveType) {
        case POINT:
            return GL11.GL_POINTS;
        case LINE:
            return GL11.GL_LINES;
        case Triangle:
            return GL11.GL_TRIANGLES;
        case Quad:
            return GL11.GL_QUADS;
        default:
            throw new IllegalArgumentException(primitiveType + "");
        }
    }

    public static int typeId(final TextureFormat texFormat) {
        switch (texFormat) {
        case RGBA8:
            return GL11.GL_RGBA8;
        case RGBA16:
            return GL11.GL_RGBA16;
        case RGBA32:
            return GL30.GL_RGBA32F;
        case DEPTH16:
            return GL14.GL_DEPTH_COMPONENT16;
        case DEPTH24:
            return GL14.GL_DEPTH_COMPONENT24;
        case DEPTH32:
            return GL14.GL_DEPTH_COMPONENT32;
        default:
            throw new IllegalArgumentException(texFormat + "");
        }
    }

    public static int typeId(final RenderConfig renderConfig) {
        switch (renderConfig) {
        case BLEND:
            return GL11.GL_BLEND;
        case CULL_FACES:
            return GL11.GL_CULL_FACE;
        case DEPTH_TEST:
            return GL11.GL_DEPTH_TEST;
        default:
            //the other RenderConfigs dont have a corresponding ID
            throw new IllegalArgumentException(renderConfig + "");
        }
    }

    public static int typeId(final PolyMode polyMode) {
        switch (polyMode) {
        case FILL:
            return GL11.GL_FILL;
        case LINE:
            return GL11.GL_LINE;
        case POINT:
            return GL11.GL_POINT;
        default:
            throw new IllegalArgumentException(polyMode + "");
        }
    }

    public static int typeId(final CullMode cullMode) {
        switch (cullMode) {
        case BACK:
            return GL11.GL_BACK;
        case FRONT:
            return GL11.GL_FRONT;
        default:
            throw new IllegalArgumentException(cullMode + "");
        }
    }

    public static int typeId(final DepthMode depthMode) {
        switch (depthMode) {
        case ALWAYS:
            return GL11.GL_ALWAYS;
        case EQUAL:
            return GL11.GL_EQUAL;
        case GREATER:
            return GL11.GL_GREATER;
        case LESS:
            return GL11.GL_LESS;
        case NEVER:
            return GL11.GL_NEVER;
        default:
            throw new IllegalArgumentException(depthMode + "");
        }
    }

    public static int typeId(final SurfaceBuffer bufferType) {
        switch (bufferType) {
        case Color:
            return GL11.GL_COLOR_BUFFER_BIT;
        case Depth:
            return GL11.GL_DEPTH_BUFFER_BIT;
        default:
            throw new IllegalArgumentException(bufferType + "");
        }
    }

    public static int typeId(final BufferUsage bufferUsage) {
        switch (bufferUsage) {
        case Dynamic:
            return GL15.GL_DYNAMIC_DRAW;
        case Static:
            return GL15.GL_STATIC_DRAW;
        case Stream:
            return GL15.GL_STREAM_DRAW;
        default:
            throw new IllegalArgumentException(bufferUsage + "");
        }
    }

    public static void flushErrors() {
        int e = 0;
        while ((e = GL11.glGetError()) != GL11.GL_NO_ERROR) {
            System.err.println("OpenGL error: " + e);
        }
        System.err.println("Flushed OpenGL-Errors");
    }

    private static int lastVertexArray = 0;
    private static final int[] lastBoundTextures = new int[32];
    private static int currentShader;

    public static void bindVertexArray(final int vertexArray, final boolean override) {
        if (vertexArray != lastVertexArray || override) {
            GL30.glBindVertexArray(vertexArray);
            lastVertexArray = vertexArray;
        }
    }

    public static void bindTexture(final int unit, final int target, final int id, final boolean override) {
        if (lastBoundTextures[unit] != id || override) {
            GL13.glActiveTexture(unit + GL13.GL_TEXTURE0);
            GL11.glBindTexture(target, id);
            lastBoundTextures[unit] = id;
        }
    }

    public static void useProgram(final int id) {
        if (currentShader != id) {
            GL20.glUseProgram(id);
            currentShader = id;
        }
    }

    public static void bindBuffer(final int target, final int buffer/* , final boolean override */) {
        GL15.glBindBuffer(target, buffer);
    }

    private static Map<RenderConfig, Boolean> featureCache = new EnumMap<>(RenderConfig.class);
    private static Map<CACHE_ENUM, Object> cache = new EnumMap<>(CACHE_ENUM.class);

    private static enum CACHE_ENUM {
        CULL_FACE_KEY, POLY_MODE_KEY, BLEND_MODE, DEPTH_FUNC, MULTISAMPLE;
    }

    public static void setEnabled(final RenderConfig feature, final boolean b) {
        final Boolean cached = featureCache.get(feature);
        if (cached == null || ((cached) != b)) {
            if (feature == RenderConfig.WRITE_COLOR) {
                GL11.glColorMask(b, b, b, b);
            } else if (feature == RenderConfig.WRITE_DEPTH) {
                GL11.glDepthMask(b);
            } else {
                final int typeId = typeId(feature);
                if (b) {
                    GL11.glEnable(typeId);
                } else {
                    GL11.glDisable(typeId);
                }
            }
            featureCache.put(feature, b);
        }
    }

    public static void setBlendMode(final BlendMode blendModeNew) {
        final BlendMode blendModeOld = (BlendMode) cache.get(CACHE_ENUM.BLEND_MODE);
        if (blendModeOld == null || blendModeOld != blendModeNew) {
            switch (blendModeNew) {
            case ADDITIVE:
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                break;
            case ALPHA:
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                break;
            case MULTIPLICATIVE:
                GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ZERO);
                break;
            default:
                throw new IllegalArgumentException("Illegal blend mode");
            }
            cache.put(CACHE_ENUM.BLEND_MODE, blendModeNew);
        }
    }

    public static void setCullMode(final CullMode mode) {
        final Object o = cache.get(CACHE_ENUM.CULL_FACE_KEY);
        if (o == null || ((CullMode) o) != mode) {
            GL11.glCullFace(typeId(mode));
            cache.put(CACHE_ENUM.CULL_FACE_KEY, mode);
        }
    }

    public static void setPolyMode(final PolyMode mode) {
        final Object o = cache.get(CACHE_ENUM.POLY_MODE_KEY);
        if (o == null || ((PolyMode) o) != mode) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, typeId(mode));
            cache.put(CACHE_ENUM.POLY_MODE_KEY, mode);
        }
    }

    public static void setDepthTestFunc(final DepthMode depthMode) {
        final Object o = cache.get(CACHE_ENUM.DEPTH_FUNC);
        if (o == null || ((DepthMode) o) != depthMode) {
            GL11.glDepthFunc(typeId(depthMode));
            cache.put(CACHE_ENUM.DEPTH_FUNC, depthMode);
        }
    }

    public static void setMultisample(final boolean b) {
        final Object o = cache.get(CACHE_ENUM.MULTISAMPLE);
        if (o == null || ((boolean) o) != b) {
            if (b) {
                GL11.glEnable(GL13.GL_MULTISAMPLE);
            } else {
                GL11.glDisable(GL13.GL_MULTISAMPLE);
            }
            cache.put(CACHE_ENUM.MULTISAMPLE, b);
        }
    }

    @Deprecated
    public static void setClearColor(final Color color) {
        setClearColor(color.getR(), color.getG(), color.getB(), color.getA());
    }

    public static void setClearColor(final float r, final float g, final float b, final float a) {
        GL11.glClearColor(r, g, b, a);
    }

    public static void clear(final SurfaceBuffer... buffers) {
        int mask = 0;
        for (final SurfaceBuffer b : buffers) {
            mask |= typeId(b);
        }
        if (mask != 0) {
            GL11.glClear(mask);
        }
    }

    //    public static enum Feature {
    //        BLEND(GL11.GL_BLEND), DEPTH_TEST(GL11.GL_DEPTH_TEST), CULL_FACES(GL11.GL_CULL_FACE),
    //        MULTISAMPLE(GL13.GL_MULTISAMPLE), SCISSORTEST(GL11.GL_SCISSOR_TEST);
    //
    //        public final int id;
    //
    //        private Feature(final int id) {
    //            this.id = id;
    //        }
    //
    //    }

}
