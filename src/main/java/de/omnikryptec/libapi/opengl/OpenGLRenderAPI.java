/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.FrameBufferStack;
import de.omnikryptec.libapi.exposed.render.IndexBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderState;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.VertexArray;
import de.omnikryptec.libapi.exposed.render.VertexBuffer;
import de.omnikryptec.libapi.exposed.render.shader.Shader;
import de.omnikryptec.libapi.exposed.window.IWindow;
import de.omnikryptec.libapi.exposed.window.SurfaceBuffer;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.libapi.opengl.buffer.GLIndexBuffer;
import de.omnikryptec.libapi.opengl.buffer.GLVertexArray;
import de.omnikryptec.libapi.opengl.buffer.GLVertexBuffer;
import de.omnikryptec.libapi.opengl.framebuffer.GLFrameBuffer;
import de.omnikryptec.libapi.opengl.shader.GLShader;
import de.omnikryptec.libapi.opengl.texture.GLTexture2D;
import de.omnikryptec.resource.MeshData.Primitive;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureData;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;

public class OpenGLRenderAPI implements RenderAPI {

    public static final IntegerKey MAJOR_VERSION = IntegerKey.next(3);
    public static final IntegerKey MINOR_VERSION = IntegerKey.next(3);

    private final GLWindow window;
    private final FrameBufferStack frameBufferStack;

    public OpenGLRenderAPI(final Settings<WindowSetting> windowsettings, final Settings<IntegerKey> apisettings) {
        this.frameBufferStack = new FrameBufferStack();
        this.window = new GLWindow(windowsettings, apisettings, this.frameBufferStack);
    }

    @Override
    public IWindow getWindow() {
        return this.window;
    }

    @Override
    public SurfaceBuffer getSurface() {
        return this.window.getDefaultFrameBuffer();
    }

    @Override
    public IndexBuffer createIndexBuffer() {
        return new GLIndexBuffer();
    }

    @Override
    public VertexBuffer createVertexBuffer() {
        return new GLVertexBuffer();
    }

    @Override
    public VertexArray createVertexArray() {
        return new GLVertexArray();
    }

    @Override
    public Texture createTexture2D(final TextureData textureData, final TextureConfig textureConfig) {
        return new GLTexture2D(textureData, textureConfig);
    }

    @Override
    public Shader createShader() {
        return new GLShader();
    }

    @Override
    public FrameBuffer createFrameBuffer(final int width, final int height, final int multisample, final int targets) {
        return new GLFrameBuffer(width, height, multisample, targets, this.frameBufferStack);
    }

    @Override
    public FrameBuffer getCurrentFrameBuffer() {
        return this.frameBufferStack.getCurrent();
    }

    @Override
    public void applyRenderState(@Nonnull final RenderState renderState) {
        OpenGLUtil.setWriteColor(renderState.isWriteColor());
        OpenGLUtil.setWriteDepth(renderState.isWriteDepth());
        OpenGLUtil.setBlendMode(renderState.getBlendMode());
        OpenGLUtil.setCullMode(renderState.getCullMode());
        OpenGLUtil.setDepthTestFunc(renderState.getDepthMode());
    }

    @Override
    public void render(final Primitive primitive, final int count, final boolean hasIndexBuffer) {
        final int typeid = OpenGLUtil.primitiveId(primitive);
        if (hasIndexBuffer) {
            GL11.glDrawElements(typeid, count, GL11.GL_UNSIGNED_INT, 0);
        } else {
            GL11.glDrawArrays(typeid, 0, count);
        }
    }

    @Override
    public void renderInstanced(final Primitive primitive, final int count, final boolean hasIndexBuffer,
            final int instanceCount) {
        final int typeid = OpenGLUtil.primitiveId(primitive);
        if (hasIndexBuffer) {
            GL31.glDrawElementsInstanced(typeid, count, GL11.GL_UNSIGNED_INT, 0, instanceCount);
        } else {
            GL31.glDrawArraysInstanced(typeid, 0, count, instanceCount);
        }
    }

    @Override
    public void printErrors() {
        OpenGLUtil.flushErrors();
    }

    @Override
    public void setPolyMode(final PolyMode polyMode) {
        OpenGLUtil.setPolyMode(polyMode);
    }

}
