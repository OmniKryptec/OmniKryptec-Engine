package de.omnikryptec.libapi.exposed.render;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.libapi.exposed.window.Window.WindowSetting;
import de.omnikryptec.libapi.opengl.OpenGLRenderAPI;
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
}
