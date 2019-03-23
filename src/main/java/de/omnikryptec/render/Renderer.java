package de.omnikryptec.render;

import de.omnikryptec.util.updater.Time;

public interface Renderer {
    
    void init(RendererContext context);
    
    default void preRender(final Time time, IProjection projection, RendererContext renderer) {
        
    }
    
    void render(Time time, IProjection projection, RendererContext renderer);
    
    default void postRender(final Time time, IProjection projection, RendererContext renderer) {
        
    }
    
    void deinit(RendererContext context);
    
    default int priority() {
        return 0;
    }
}
