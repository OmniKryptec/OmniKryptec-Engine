package de.omnikryptec.graphics.render;

import java.util.HashMap;
import java.util.Map;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class Viewport {

    private final Map<Renderer, DisplayList> renderables;
    private final IProjection projection;

    private final RendererSet rendererSet;

    public Viewport(final IProjection projection, final RendererSet rendererSet) {
        this.renderables = new HashMap<>();
        this.projection = Util.ensureNonNull(projection);
        this.rendererSet = Util.ensureNonNull(rendererSet);
    }

    public void render(final Time time, final FrameBuffer target, final Settings<?> renderSettings) {
        if (target != null) {
            target.bindFrameBuffer();
        }
        for (final Renderer renderer : this.renderables.keySet()) {
            renderer.render(time, this.projection, renderer.supportsObjects() ? this.renderables.get(renderer) : null,
                    renderSettings);
        }
        if (target != null) {
            target.unbindFrameBuffer();
        }
    }

    public void add(final Renderer renderer, final Object... objs) {
        Util.ensureNonNull(objs);
        if (!this.rendererSet.supports(renderer)) {
            throw new IllegalArgumentException("renderer not supported");
        }
        if (!renderer.supportsObjects()) {
            throw new IllegalArgumentException("renderer is not an object renderer");
        }
        DisplayList list = this.renderables.get(renderer);
        if (list == null) {
            list = Util.ensureNonNull(renderer.createRenderList());
            this.renderables.put(renderer, list);
        }
        for (final Object o : objs) {
            list.addObject(o);
        }
    }

    public IProjection getProjection() {
        return projection;
    }
    
    public void clear() {
        this.renderables.clear();
    }
    
    //Might use later
    public boolean refill() {
        return true;
    }

}
