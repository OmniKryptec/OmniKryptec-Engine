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

package de.omnikryptec.libapi.opengl;

import java.util.EnumMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

import de.omnikryptec.graphics.shader.base.parser.ShaderParser.ShaderType;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.RenderState.BlendMode;
import de.omnikryptec.resource.MeshData.PrimitiveType;
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
    
    private static int lastVertexArray = 0;
    
    public static void bindVertexArray(final int vertexArray, final boolean override) {
        if (vertexArray != lastVertexArray || override) {
            GL30.glBindVertexArray(vertexArray);
            lastVertexArray = vertexArray;
        }
    }
    
    private static final int[] lastBoundTextures = new int[32];
    
    public static void bindTexture(final int unit, final int target, final int id, final boolean override) {
        if (lastBoundTextures[unit] != id || override) {
            GL13.glActiveTexture(unit + GL13.GL_TEXTURE0);
            GL11.glBindTexture(target, id);
            lastBoundTextures[unit] = id;
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
    
    private static int currentShader;
    
    public static void useProgram(final int id) {
        if (currentShader != id) {
            GL20.glUseProgram(id);
            currentShader = id;
        }
    }
    
    public static int typeId(PrimitiveType type) {
        switch (type) {
        case Quad:
            return GL11.GL_QUADS;
        case Triangle:
            return GL11.GL_TRIANGLES;
        default:
            throw new IllegalArgumentException(type + "");
        }
    }
    
    //TODO remake below this
    
    private static Map<Feature, Boolean> featureCache = new EnumMap<>(Feature.class);
    private static Map<CACHE_ENUM, Object> cache = new EnumMap<>(CACHE_ENUM.class);
    
    public static boolean isFeatureEnabled(final Feature f) {
        return featureCache.get(f) == null ? false : featureCache.get(f);
    }
    
    public static void setEnabled(final Feature feature, final boolean b) {
        final Boolean cached = featureCache.get(feature);
        if (cached == null || ((cached) != b)) {
            if (b) {
                GL11.glEnable(feature.id);
                featureCache.put(feature, true);
            } else {
                GL11.glDisable(feature.id);
                featureCache.put(feature, false);
            }
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
            GL11.glCullFace(mode.id);
            cache.put(CACHE_ENUM.CULL_FACE_KEY, mode);
        }
    }
    
    public static void setScissor(final int x, final int y, final int width, final int height) {
        GL11.glScissor(x, y, width, height);
    }
    
    public static void setDepthMask(final boolean b) {
        final Object o = cache.get(CACHE_ENUM.DEPTH_MASK_KEY);
        if (o == null || ((boolean) o) != b) {
            GL11.glDepthMask(b);
            cache.put(CACHE_ENUM.DEPTH_MASK_KEY, b);
        }
    }
    
    public static void setPolyMode(final PolyMode mode) {
        final Object o = cache.get(CACHE_ENUM.POLY_MODE_KEY);
        if (o == null || ((PolyMode) o) != mode) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, mode.id);
            cache.put(CACHE_ENUM.POLY_MODE_KEY, mode);
        }
    }
    
    public static void setClearColor(final Color color) {
        setClearColor(color.getR(), color.getG(), color.getB(), color.getA());
    }
    
    public static void setClearColor(final float r, final float g, final float b, final float a) {
        GL11.glClearColor(r, g, b, a);
    }
    
    public static void clear(final BufferType... buffers) {
        int mask = 0;
        for (final BufferType b : buffers) {
            mask |= b.id;
        }
        GL11.glClear(mask);
    }
    
    public static enum CullMode {
        BACK(GL11.GL_BACK), FRONT(GL11.GL_FRONT);
        
        public final int id;
        
        private CullMode(final int id) {
            this.id = id;
        }
    }
    
    public static enum PolyMode {
        FILL(GL11.GL_FILL), LINE(GL11.GL_LINE), POINT(GL11.GL_POINT);
        
        public final int id;
        
        private PolyMode(final int id) {
            this.id = id;
        }
    }
    
    public static enum BufferType {
        COLOR(GL11.GL_COLOR_BUFFER_BIT), DEPTH(GL11.GL_DEPTH_BUFFER_BIT), @Deprecated
        ACCUM(GL11.GL_ACCUM_BUFFER_BIT), STENCIL(GL11.GL_STENCIL_BUFFER_BIT);
        
        public final int id;
        
        private BufferType(final int id) {
            this.id = id;
        }
    }
    
    public static enum Feature {
        BLEND(GL11.GL_BLEND), DEPTH_TEST(GL11.GL_DEPTH_TEST), CULL_FACES(GL11.GL_CULL_FACE),
        MULTISAMPLE(GL13.GL_MULTISAMPLE), SCISSORTEST(GL11.GL_SCISSOR_TEST);
        
        public final int id;
        
        private Feature(final int id) {
            this.id = id;
        }
        
    }
    
    private static enum CACHE_ENUM {
        DEPTH_MASK_KEY, CULL_FACE_KEY, POLY_MODE_KEY, BLEND_MODE;
    }
    
}
