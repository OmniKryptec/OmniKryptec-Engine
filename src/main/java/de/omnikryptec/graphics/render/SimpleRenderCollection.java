package de.omnikryptec.graphics.render;

import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class SimpleRenderCollection implements RenderCollection {
    
    private ListMultimap<Renderer, RenderedObject> objects;
    
    public SimpleRenderCollection() {
        this.objects = ArrayListMultimap.create();
    }
    
    @Override
    public void fillViewport(Viewport viewport) {
        List<Renderer> vpRenderer = viewport.getRendererSet().getSupportedRenderer();
        for (Renderer r : vpRenderer) {
            viewport.add(r, objects.get(r));
        }
        viewport.flip();
    }
    
    @Override
    public void add(Renderer renderer, RenderedObject robj) {
        objects.put(renderer, robj);
    }
    
    @Override
    public void remove(Renderer renderer, RenderedObject robj) {
        objects.remove(renderer, robj);
    }
    
}
