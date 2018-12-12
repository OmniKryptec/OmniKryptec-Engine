package de.omnikryptec.libapi.exposed.render;

import de.omnikryptec.libapi.exposed.window.Window;
import de.omnikryptec.libapi.opengl.OpenGLRendererAPI;

public interface RenderAPI {
    public static final Class<OpenGLRendererAPI> OpenGL = OpenGLRendererAPI.class;

    void window_setHints(Window window);
    void window_Resized(Window window, int width, int height);
    void window_swap(Window window);
}
