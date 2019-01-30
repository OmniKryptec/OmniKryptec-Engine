package de.omnikryptec.graphics.render;

//TODO what about moving objects?
public interface RenderCollection {
    
    void fillViewport(final Viewport viewport);
    void add(Renderer renderer, RenderedObject robj);
    void remove(Renderer renderer, RenderedObject robj);
}
