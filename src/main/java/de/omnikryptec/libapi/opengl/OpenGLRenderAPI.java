package de.omnikryptec.libapi.opengl;

import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.libapi.exposed.window.Window.WindowSetting;
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
    
}
