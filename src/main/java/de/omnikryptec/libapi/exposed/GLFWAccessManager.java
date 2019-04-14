package de.omnikryptec.libapi.exposed;

import java.lang.reflect.Constructor;

import org.lwjgl.glfw.GLFW;

import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.window.WindowSetting;
import de.omnikryptec.util.Logger;
import de.omnikryptec.util.settings.IntegerKey;
import de.omnikryptec.util.settings.Settings;

public class GLFWAccessManager {
    
    private RenderAPI renderApi;
    private final Logger logger = Logger.getLogger(getClass());
    
    GLFWAccessManager() {
    }
    
    public void setRenderer(Class<? extends RenderAPI> clazz, Settings<WindowSetting> windowSettings,
            Settings<IntegerKey> apiSettings) {
        if (isRendererSet()) {
            throw new IllegalStateException("Renderer is already set!");
        }
        try {
            final Constructor<? extends RenderAPI> renderApiConstructor = clazz
                    .getConstructor(windowSettings.getClass(), apiSettings.getClass());
            renderApiConstructor.setAccessible(true);
            renderApi = renderApiConstructor.newInstance(windowSettings, apiSettings);
            logger.info("Set the RenderAPI to " + clazz.getSimpleName());
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException("Invalid RendererAPI: Missing constructor", ex);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean isRendererSet() {
        return renderApi != null;
    }
    
    public RenderAPI getRenderAPI() {
        return renderApi;
    }
    
    public void pollEvents() {
        GLFW.glfwPollEvents();
    }
    
    /**
     * Returns the value of the GLFW timer. The timer measures time elapsed since
     * GLFW was initialized.
     * <p>
     * The resolution of the timer is system dependent, but is usually on the order
     * of a few micro- or nanoseconds. It uses the highest-resolution monotonictime
     * source on each supported platform.
     *
     * @return the current value, in seconds, or zero if an error occurred
     */
    public double getTime() {
        return GLFW.glfwGetTime();
    }
}
