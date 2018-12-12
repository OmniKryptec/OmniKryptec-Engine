package de.omnikryptec.libapi.opengl;

import org.lwjgl.glfw.GLFW;

import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;

public class OpenGLRendererAPI implements RenderAPI {

    public static final IntegerKey MAJOR_VERSION = new IntegerKey(0, 1);
    public static final IntegerKey MINOR_VERSION = new IntegerKey(1, 0);
    public static final IntegerKey VSYNC = new IntegerKey(2, true);

    private OpenGLRendererAPI(Settings<IntegerKey> apisettings) {

    }

    @Override
    public void window_setHints(Window window) {
    }

    @Override
    public void window_Resized(Window window, int width, int height) {
    }

    @Override
    public void window_swap(Window window) {
        GLFW.glfwSwapBuffers(window.getWindowID());
    }

}
