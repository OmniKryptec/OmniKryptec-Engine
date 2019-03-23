package de.omnikryptec.render.storage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.RendererContext;
import de.omnikryptec.render.Renderer;
import de.omnikryptec.util.updater.Time;

public class RendererManager {
    
    private static final Comparator<Renderer> RENDERER_PRIORITY_COMPARATOR = (e1, e2) -> e2.priority() - e1.priority();
    
    private List<Renderer> renderers;
    
    public RendererManager() {
        renderers = new ArrayList<>();
    }
    
    public void addRenderer(Renderer renderer) {
        renderers.add(renderer);
        renderers.sort(RENDERER_PRIORITY_COMPARATOR);
        renderer.init();
    }
    
    public void removeRenderer(Renderer renderer) {
        renderer.deinit();
        renderers.remove(renderer);
    }
    
    public void preRender(Time time, IProjection projection, RendererContext context) {
        for (Renderer r : renderers) {
            r.preRender(time, projection, context);
        }
    }
    
    public void render(Time time, IProjection projection, RendererContext context) {
        for (Renderer r : renderers) {
            r.render(time, projection, context);
        }
    }
    
    public void postRender(Time time, IProjection projection, RendererContext context) {
        for (Renderer r : renderers) {
            r.postRender(time, projection, context);
        }
    }
}
