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

import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.IndexBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
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

    public OpenGLRenderAPI(final Settings<IntegerKey> apisettings) {
        this.apisettings = apisettings;
    }

    @Override
    public Window createWindow(final Settings<WindowSetting> windowsettings) {
        return new OpenGLWindow(windowsettings, this.apisettings);
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

}
