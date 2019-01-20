package de.omnikryptec.graphics.render;

import de.omnikryptec.util.settings.Settings;

public interface Renderer {
    
    void render(IProjection projection, RenderList<?> objs, Settings<?> renderSettings);
    
    RenderList<?> createRenderList();
    
}
