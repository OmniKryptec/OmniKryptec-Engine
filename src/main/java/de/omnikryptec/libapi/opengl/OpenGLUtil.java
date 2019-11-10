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

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Map;

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL44;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GL46;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.RenderAPI.BufferUsage;
import de.omnikryptec.libapi.exposed.render.RenderAPI.PolyMode;
import de.omnikryptec.libapi.exposed.render.RenderAPI.SurfaceBufferType;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.RenderState.BlendMode;
import de.omnikryptec.libapi.exposed.render.RenderState.CullMode;
import de.omnikryptec.libapi.exposed.render.RenderState.DepthMode;
import de.omnikryptec.libapi.opengl.texture.GLTexture2D;
import de.omnikryptec.resource.MeshData.Primitive;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureConfig.MagMinFilter;
import de.omnikryptec.resource.TextureConfig.WrappingMode;
import de.omnikryptec.resource.TextureData;
import de.omnikryptec.resource.parser.shader.ShaderParser.ShaderType;
import de.omnikryptec.util.Logger;
import de.omnikryptec.util.Logger.LogType;

public class OpenGLUtil {

    private static final Logger logger = Logger.getLogger(OpenGLUtil.class);

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

    public static int shaderTypeId(final ShaderType shaderType) {
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

    public static int primitiveId(final Primitive primitiveType) {
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

    public static int textureFormatId(final FBAttachmentFormat texFormat) {
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

    public static int polyModeId(final PolyMode polyMode) {
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

    public static int cullModeId(final CullMode cullMode) {
        switch (cullMode) {
        case BACK:
            return GL11.GL_BACK;
        case FRONT:
            return GL11.GL_FRONT;
        default:
            throw new IllegalArgumentException(cullMode + "");
        }
    }

    public static int depthModeId(final DepthMode depthMode) {
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

    public static int surfaceBufferTypeId(final SurfaceBufferType bufferType) {
        switch (bufferType) {
        case Color:
            return GL11.GL_COLOR_BUFFER_BIT;
        case Depth:
            return GL11.GL_DEPTH_BUFFER_BIT;
        default:
            throw new IllegalArgumentException(bufferType + "");
        }
    }

    public static int bufferUsageId(final BufferUsage bufferUsage) {
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

    public static int decodeMagMin(final MagMinFilter filter) {
        switch (filter) {
        case Linear:
            return GL11.GL_LINEAR;
        case Nearest:
            return GL11.GL_NEAREST;
        default:
            throw new IllegalArgumentException();
        }
    }

    public static int decodeWrap(final WrappingMode mode) {
        switch (mode) {
        case ClampToEdge:
            return GL12.GL_CLAMP_TO_EDGE;
        case Repeat:
            return GL11.GL_REPEAT;
        default:
            throw new IllegalArgumentException();
        }
    }

    public static void flushErrors() {
        int e = 0;
        int found = 0;
        while ((e = GL11.glGetError()) != GL11.GL_NO_ERROR) {
            logger.error("OpenGL error: " + (LibAPIManager.debug() ? searchConstants(e) : e));
            found++;
        }
        if (found != 0) {
            throw new RuntimeException("Stopping due to " + found + " OpenGL error(s)");
        }
        if (found == 0) {
            logger.debug("No OpenGL errors found!");
        }
    }

    private static final Class<?>[] constantsClasses = { GL11.class, GL12.class, GL13.class, GL14.class, GL15.class,
            GL20.class, GL21.class, GL30.class, GL31.class, GL32.class, GL33.class, GL40.class, GL41.class, GL42.class,
            GL43.class, GL44.class, GL45.class, GL46.class };

    private static String searchConstants(final int i) {
        for (final Class<?> c : constantsClasses) {
            final Field[] fields = c.getFields();
            for (final Field f : fields) {
                try {
                    if (i == f.getInt(null)) {
                        return f.getName();
                    }
                } catch (final IllegalArgumentException e) {
                } catch (final IllegalAccessException e) {
                }
            }
        }
        throw new IllegalArgumentException("Constant with value '" + i + "' not found");
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

    public static void configureTexture(final TextureConfig config) {
        if (config.mipmap() || config.anisotropicValue() > 0) {
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            if (config.anisotropicValue() > 0) {
                if (!GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
                    Logger.log(GLTexture2D.class, LogType.Warning,
                            "GL_EXT_texture_filter_anisotropic is not supported");
                } else {
                    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
                    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
                            config.anisotropicValue());
                }
            }
        } else {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
                    OpenGLUtil.decodeMagMin(config.magFilter()));
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                    OpenGLUtil.decodeMagMin(config.minFilter()));
        }
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, OpenGLUtil.decodeWrap(config.wrappingMode()));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, OpenGLUtil.decodeWrap(config.wrappingMode()));
    }

    public static void loadTexture(final TextureData texture) {
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, texture.getWidth(), texture.getHeight(), 0, GL12.GL_BGRA,
                GL11.GL_UNSIGNED_BYTE, texture.getBuffer());
    }

    private static Map<CACHE_ENUM, Object> cache = new EnumMap<>(CACHE_ENUM.class);
    private static boolean oldColor, oldDepth;

    private static enum CACHE_ENUM {
        CULL_FACE_KEY, POLY_MODE_KEY, BLEND_MODE, DEPTH_FUNC, MULTISAMPLE;
    }

    public static void setWriteColor(final boolean color) {
        if (color != oldColor) {
            oldColor = color;
            GL11.glColorMask(color, color, color, color);
        }
    }

    public static void setWriteDepth(final boolean depth) {
        if (depth != oldDepth) {
            oldDepth = depth;
            GL11.glDepthMask(depth);
        }
    }

    public static void setBlendMode(final BlendMode blendModeNew) {
        final BlendMode blendModeOld = (BlendMode) cache.get(CACHE_ENUM.BLEND_MODE);
        if (blendModeOld == null || blendModeOld != blendModeNew) {
            if (blendModeNew == BlendMode.OFF) {
                GL11.glDisable(GL11.GL_BLEND);
            } else {
                if (blendModeOld == null || blendModeOld == BlendMode.OFF) {
                    GL11.glEnable(GL11.GL_BLEND);
                }
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
            }
            cache.put(CACHE_ENUM.BLEND_MODE, blendModeNew);
        }
    }

    public static void setCullMode(final CullMode mode) {
        final CullMode o = (CullMode) cache.get(CACHE_ENUM.CULL_FACE_KEY);
        if (o == null || (o) != mode) {
            if (mode == CullMode.OFF) {
                GL11.glDisable(GL11.GL_CULL_FACE);
            } else {
                if (o == null || o == CullMode.OFF) {
                    GL11.glEnable(GL11.GL_CULL_FACE);
                }
                GL11.glCullFace(cullModeId(mode));
            }
            cache.put(CACHE_ENUM.CULL_FACE_KEY, mode);
        }
    }

    public static void setPolyMode(final PolyMode mode) {
        final Object o = cache.get(CACHE_ENUM.POLY_MODE_KEY);
        if (o == null || ((PolyMode) o) != mode) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, polyModeId(mode));
            cache.put(CACHE_ENUM.POLY_MODE_KEY, mode);
        }
    }

    public static void setDepthTestFunc(DepthMode depthMode) {
        if (depthMode == DepthMode.DEFAULT) {
            depthMode = DepthMode.LESS;
        }
        final DepthMode o = (DepthMode) cache.get(CACHE_ENUM.DEPTH_FUNC);
        if (o == null || o != depthMode) {
            if (depthMode == DepthMode.OFF) {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
            } else {
                if (o == null || o == DepthMode.OFF) {
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                }
                GL11.glDepthFunc(depthModeId(depthMode));
            }
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

    public static void setClearColor(final float r, final float g, final float b, final float a) {
        GL11.glClearColor(r, g, b, a);
    }

    public static void clear(final SurfaceBufferType... buffers) {
        int mask = 0;
        for (final SurfaceBufferType b : buffers) {
            mask |= surfaceBufferTypeId(b);
        }
        if (mask != 0) {
            GL11.glClear(mask);
        }
    }

    public static int indexToAttachment(final int attachment) {
        if (attachment == FBTarget.DEPTH_ATTACHMENT_INDEX) {
            return GL30.GL_DEPTH_ATTACHMENT;
        }
        return GL30.GL_COLOR_ATTACHMENT0 + attachment;
    }

    public static int indexToBufferBit(final int attachment) {
        if (attachment == FBTarget.DEPTH_ATTACHMENT_INDEX) {
            return GL11.GL_DEPTH_BUFFER_BIT;
        }
        return GL11.GL_COLOR_BUFFER_BIT;
    }

}
