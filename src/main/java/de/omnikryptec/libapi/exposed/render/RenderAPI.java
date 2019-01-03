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
import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.libapi.exposed.window.Window.WindowSetting;
import de.omnikryptec.libapi.opengl.OpenGLRenderAPI;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureData;
import de.omnikryptec.util.settings.Settings;

public interface RenderAPI {
    public static final Class<OpenGLRenderAPI> OpenGL = OpenGLRenderAPI.class;
    
    public static RenderAPI get() {
        return LibAPIManager.active().getRenderAPI();
    }
    
    public static enum Type {
        FLOAT
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
}
