package de.omnikryptec.render.frame;

import de.omnikryptec.render.Renderer;
import de.omnikryptec.render.storage.RenderedObject;

//TODO what about moving objects?
@Deprecated
public interface RenderCollection {
    
    void fillViewport(final Viewport viewport);

    void add(Renderer renderer, RenderedObject robj);

    void remove(Renderer renderer, RenderedObject robj);
}
