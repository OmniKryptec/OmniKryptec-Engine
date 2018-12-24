package de.omnikryptec.libapi.exposed.render;

import de.omnikryptec.graphics.shader.base.parser.ShaderParser.ShaderType;
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
    
    Window createWindow(Settings<WindowSetting> windowsettings);
    
    IndexBuffer createIndexBuffer();
    
    VertexBuffer createVertexBuffer();
    
    VertexArray createVertexArray();
    
    Texture createTexture2D(TextureData textureData, TextureConfig textureConfig);
    
    Shader createShader(ShaderType type, String source);
}
