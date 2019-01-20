package de.omnikryptec.graphics.render;

import java.util.HashMap;
import java.util.Map;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.settings.Settings;

public class Viewport {
    
    private Map<Renderer, RenderList<?>> renderables;
    private IProjection projection;
    
    public Viewport(IProjection projection) {
        this.renderables = new HashMap<>();
        this.projection = projection;
    }
    
    public void render(FrameBuffer target, Settings<?> renderSettings) {
        target.bindFrameBuffer();
        for (Renderer renderer : renderables.keySet()) {
            renderer.render(projection, renderables.get(renderer), renderSettings);
        }
        target.unbindFrameBuffer();
    }
    
    public void add(Renderer renderer, Object obj) {
        RenderList<?> list = renderables.get(renderer);
        if (list == null) {
            list = Util.ensureNonNull(renderer.createRenderList());
            renderables.put(renderer, list);
        }
        list.addObject(obj);
    }
    
}
