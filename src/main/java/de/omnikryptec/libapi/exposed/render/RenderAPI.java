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

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.shader.Shader;
import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.libapi.exposed.window.Window.WindowSetting;
import de.omnikryptec.libapi.opengl.OpenGLRenderAPI;
import de.omnikryptec.resource.MeshData.Primitive;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureData;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.settings.Settings;

public interface RenderAPI {
    public static final Class<OpenGLRenderAPI> OpenGL = OpenGLRenderAPI.class;

    /**
     * The currently set {@link RenderAPI}.
     * <p>
     * Note: this is the same as calling<br>
     * <code>LibAPIManager.instance().getRenderAPI()</code>
     * </p>
     *
     * @return the current RenderAPI
     */
    public static RenderAPI get() {
        return LibAPIManager.instance().getRenderAPI();
    }

    public static enum Type {
        FLOAT
    }

    public static enum SurfaceBuffer {
        Depth, Color;
    }

    public static enum BufferUsage {
        Static, Dynamic, Stream;
    }

    /**
     * Creates a {@link Window} with the specified settings, compatible and ready to
     * be drawn on by this {@link RenderAPI}
     *
     * @param windowsettings window settings
     * @return a new window
     */
    Window createWindow(Settings<WindowSetting> windowsettings);

    /**
     * Returns the currently used {@link Window}
     *
     * @return
     */
    Window getWindow();

    /**
     * Creates a new {@link IndexBuffer}
     *
     * @return indexbuffer
     */
    IndexBuffer createIndexBuffer();

    /**
     * Creates a new {@link VertexBuffer}
     *
     * @return vertexbuffer
     */
    VertexBuffer createVertexBuffer();

    /**
     * Creates a new {@link VertexArray}
     *
     * @return vertexarray
     */
    VertexArray createVertexArray();

    /**
     * Creates a new {@link Texture} that represents a 2-dimensional image
     *
     * @param textureData   the texture
     * @param textureConfig texture config
     * @return texture
     */
    Texture createTexture2D(TextureData textureData, TextureConfig textureConfig);

    /**
     * Creates a new {@link Shader} program (e.g. bundle of vertex- and
     * fragmentshader)
     *
     * @return shaderprogram
     */
    Shader createShader();

    /**
     * Creates a new {@link FrameBuffer} to be used with this {@link RenderAPI}.
     *
     * @param width       the width of the FB
     * @param height      the height of the FB
     * @param multisample for values greater 0, the FB will be multisampled
     * @param targets     the amount of targets this FB will use
     * @return a new FrameBuffer
     *
     * @see FrameBuffer#assignTarget(int, FBTarget)
     */
    FrameBuffer createFrameBuffer(int width, int height, int multisample, int targets);

    void applyRenderState(RenderState renderState);

    default void render(final VertexArray vertexArray, final Primitive primitive, final int elementCount) {
        vertexArray.bindArray();
        render(primitive, elementCount, vertexArray.hasIndexBuffer());
        vertexArray.unbindArray();
    }

    void render(Primitive primitive, int elementCount, boolean hasIndexBuffer);

    default void renderInstanced(final VertexArray vertexArray, final Primitive primitive, final int elementCount,
            final int instanceCount) {
        vertexArray.bindArray();
        renderInstanced(primitive, elementCount, vertexArray.hasIndexBuffer(), instanceCount);
        vertexArray.unbindArray();
    }

    void renderInstanced(Primitive primitive, int elementCount, boolean hasIndexBuffer, int instanceCount);

    void clear(SurfaceBuffer... buffer);

    default void setClearColor(final Color color) {
        setClearColor(color.getR(), color.getG(), color.getB(), color.getA());
    }

    void setClearColor(float r, float g, float b, float a);
}
