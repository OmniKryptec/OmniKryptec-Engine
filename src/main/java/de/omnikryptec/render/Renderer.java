package de.omnikryptec.render;

import de.omnikryptec.libapi.exposed.window.SurfaceBuffer;
import de.omnikryptec.util.updater.Time;

public interface Renderer {
    
    void init(RendererContext context);
    
    default void preRender(final Time time, IProjection projection, RendererContext context) {
        
    }
    
    void render(Time time, IProjection projection, RendererContext context);
    
    default void postRender(final Time time, IProjection projection, RendererContext context) {
        
    }
    
    default void createAndResizeFBO(RendererContext context, SurfaceBuffer screen) {
        
    }
    
    void deinit(RendererContext context);
    
    default int priority() {
        return 0;
    }
}
