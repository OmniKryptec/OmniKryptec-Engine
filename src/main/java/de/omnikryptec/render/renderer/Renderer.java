package de.omnikryptec.render.renderer;

import de.omnikryptec.libapi.exposed.window.SurfaceBuffer;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.util.updater.Time;

public interface Renderer {
    
    void init(LocalRendererContext context);
    
    default void preRender(final Time time, IProjection projection, LocalRendererContext context) {
        
    }
    
    void render(Time time, IProjection projection, LocalRendererContext context);
    
    default void postRender(final Time time, IProjection projection, LocalRendererContext context) {
        
    }
    
    default void createOrResizeFBO(LocalRendererContext context, SurfaceBuffer screen) {
        
    }
    
    void deinit(LocalRendererContext context);
    
    default int priority() {
        return 0;
    }
}
