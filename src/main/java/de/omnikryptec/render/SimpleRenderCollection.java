package de.omnikryptec.render;

import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class SimpleRenderCollection implements RenderCollection {

    private final ListMultimap<Renderer, RenderedObject> objects;

    public SimpleRenderCollection() {
        this.objects = ArrayListMultimap.create();
    }

    @Override
    public void fillViewport(final Viewport viewport) {
        final List<Renderer> vpRenderer = viewport.getRendererSet().getSupportedRenderer();
        for (final Renderer r : vpRenderer) {
            viewport.add(r, this.objects.get(r));
        }
        viewport.flip();
    }

    @Override
    public void add(final Renderer renderer, final RenderedObject robj) {
        this.objects.put(renderer, robj);
    }

    @Override
    public void remove(final Renderer renderer, final RenderedObject robj) {
        this.objects.remove(renderer, robj);
    }

}
