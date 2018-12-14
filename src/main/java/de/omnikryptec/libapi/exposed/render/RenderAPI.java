package de.omnikryptec.libapi.exposed.render;

import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.libapi.exposed.window.Window.WindowSetting;
import de.omnikryptec.libapi.opengl.OpenGLRenderAPI;
import de.omnikryptec.util.settings.Settings;

public interface RenderAPI {
    public static final Class<OpenGLRenderAPI> OpenGL = OpenGLRenderAPI.class;

    Window createWindow(Settings<WindowSetting> windowsettings);
}
