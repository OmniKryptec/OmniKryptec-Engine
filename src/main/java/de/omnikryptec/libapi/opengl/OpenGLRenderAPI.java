package de.omnikryptec.libapi.opengl;

import de.omnikryptec.libapi.exposed.render.IndexBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.VertexArray;
import de.omnikryptec.libapi.exposed.render.VertexBuffer;
import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.libapi.exposed.window.Window.WindowSetting;
import de.omnikryptec.libapi.opengl.buffer.GLIndexBuffer;
import de.omnikryptec.libapi.opengl.buffer.GLVertexArray;
import de.omnikryptec.libapi.opengl.buffer.GLVertexBuffer;
import de.omnikryptec.libapi.opengl.texture.GLTexture2D;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureData;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;

public class OpenGLRenderAPI implements RenderAPI {

    public static final IntegerKey MAJOR_VERSION = new IntegerKey(0, 1);
    public static final IntegerKey MINOR_VERSION = new IntegerKey(1, 0);

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
    public Texture createTexture2D(TextureData textureData, TextureConfig textureConfig) {
        return new GLTexture2D(textureData, textureConfig);
    }

}
