package de.omnikryptec.graphics.render;

import de.omnikryptec.util.settings.Settings;

public interface Renderer {
    
    default void preRender(Settings<?> renderSettings) {
        
    }
    
    void render(IProjection projection, DisplayList objs, Settings<?> renderSettings);
    
    default void postRender(Settings<?> renderSettings) {
        
    }
    
    DisplayList createRenderList();
    
}
