package de.omnikryptec.render;

import de.omnikryptec.util.updater.Time;

public interface Renderer {
    
    void init();
    
    default void preRender(final Time time, IProjection projection, RendererContext renderer) {
        
    }
    
    void render(Time time, IProjection projection, RendererContext renderer);
    
    default void postRender(final Time time, IProjection projection, RendererContext renderer) {
        
    }
    
    void deinit();
    
    default int priority() {
        return 0;
    }
}
