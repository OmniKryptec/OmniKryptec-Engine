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

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;

import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.IndexBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderState;
import de.omnikryptec.libapi.exposed.render.RenderState.RenderConfig;
import de.omnikryptec.libapi.exposed.render.Renderable;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.VertexArray;
import de.omnikryptec.libapi.exposed.render.VertexBuffer;
import de.omnikryptec.libapi.exposed.render.shader.Shader;
import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.libapi.exposed.window.Window.WindowSetting;
import de.omnikryptec.libapi.opengl.buffer.GLIndexBuffer;
import de.omnikryptec.libapi.opengl.buffer.GLVertexArray;
import de.omnikryptec.libapi.opengl.buffer.GLVertexBuffer;
import de.omnikryptec.libapi.opengl.shader.GLShader;
import de.omnikryptec.libapi.opengl.texture.GLTexture2D;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureData;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;

public class OpenGLRenderAPI implements RenderAPI {
    
    public static final IntegerKey MAJOR_VERSION = IntegerKey.next(1);
    public static final IntegerKey MINOR_VERSION = IntegerKey.next(0);
    
    private final Settings<IntegerKey> apisettings;
    private Window window;
    
    public OpenGLRenderAPI(final Settings<IntegerKey> apisettings) {
        this.apisettings = apisettings;
    }
    
    @Override
    public Window createWindow(final Settings<WindowSetting> windowsettings) {
        return this.window = new OpenGLWindow(windowsettings, this.apisettings);
    }
    
    @Override
    public Window getWindow() {
        return this.window;
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
    public FrameBuffer createFrameBuffer(final int width, final int height, final int multisample,
            final FBTarget... targets) {
        return new GLFrameBuffer(width, height, multisample, targets);
    }
    
    @Override
    public void applyRenderState(@Nonnull final RenderState renderState) {
        for (final RenderConfig config : RenderConfig.values()) {
            OpenGLUtil.setEnabled(config, renderState.isEnable(config));
        }
        if (null != renderState.getBlendMode()) {
            OpenGLUtil.setBlendMode(renderState.getBlendMode());
        }
        if (null != renderState.getPolyMode()) {
            OpenGLUtil.setPolyMode(renderState.getPolyMode());
        }
        if (null != renderState.getCullMode()) {
            OpenGLUtil.setCullMode(renderState.getCullMode());
        }
        if (null != renderState.getDepthMode()) {
            OpenGLUtil.setDepthTestFunc(renderState.getDepthMode());
        }
    }
    
    @Override
    public void render(@Nonnull final Renderable mesh) {
        mesh.bindRenderable();
        final int typeid = OpenGLUtil.typeId(mesh.primitive());
        if (mesh.hasIndexBuffer()) {
            GL11.glDrawElements(typeid, mesh.elementCount(), GL11.GL_UNSIGNED_INT, 0);
        } else {
            GL11.glDrawArrays(typeid, 0, mesh.elementCount());
        }
        mesh.unbindRenderable();
    }
    
    @Override
    public void renderInstanced(@Nonnull final Renderable mesh, final int count) {
        mesh.bindRenderable();
        final int typeid = OpenGLUtil.typeId(mesh.primitive());
        if (mesh.hasIndexBuffer()) {
            GL31.glDrawElementsInstanced(typeid, mesh.elementCount(), GL11.GL_UNSIGNED_INT, 0, count);
        } else {
            GL31.glDrawArraysInstanced(typeid, 0, mesh.elementCount(), count);
        }
        mesh.unbindRenderable();
    }
    
    @Override
    public void clear(SurfaceBuffer... buffers) {
        OpenGLUtil.clear(buffers);
    }
    
    @Override
    public void setClearColor(float r, float g, float b, float a) {
        OpenGLUtil.setClearColor(r, g, b, a);
    }
    
}
