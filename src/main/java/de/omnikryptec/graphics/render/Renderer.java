package de.omnikryptec.graphics.render;

import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public interface Renderer {
    
    default void preRender(final Time time, final Settings<?> renderSettings) {
        
    }
    
    void render(Time time, IProjection projection, DisplayList objs, Settings<?> renderSettings);
    
    default void postRender(final Time time, final Settings<?> renderSettings) {
        
    }
    
    DisplayList createRenderList();
    
    default boolean supportsObjects() {
        return true;
    }
}
