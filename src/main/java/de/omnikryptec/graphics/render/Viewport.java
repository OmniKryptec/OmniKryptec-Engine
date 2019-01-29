package de.omnikryptec.graphics.render;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3fc;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.data.Struct3f;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class Viewport {
    
    private final RendererSet rendererSet;
    private final Map<Renderer, DisplayList> renderables;
    
    //TODO setter/initial value
    private IProjection projection;
    private Struct3f position;
    
    public Viewport(final RendererSet rendererSet) {
        this.renderables = new HashMap<>();
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
    
    public void add(final Renderer renderer, final Collection<? extends RenderedObject> objs) {
        Util.ensureNonNull(objs);
        if (objs.isEmpty()) {
            return;
        }
        DisplayList list = checkAndGetDisplayList(renderer);
        for (RenderedObject r : objs) {
            addUnchecked(list, r);
        }
    }
    
    public void add(final Renderer renderer, RenderedObject obj) {
        Util.ensureNonNull(obj);
        addUnchecked(checkAndGetDisplayList(renderer), obj);
    }
    
    private void addUnchecked(DisplayList list, RenderedObject r) {
        float radius = r.maxBoundRadius();
        Vector3fc pos = r.position();
        if (projection.getFrustumTester().testSphere(pos, radius)) {
            list.addObject(r);
        }
    }
    
    private DisplayList checkAndGetDisplayList(Renderer renderer) {
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
        return list;
    }
    
    public IProjection getProjection() {
        return projection;
    }
    
    public Struct3f getPosition() {
        return position;
    }
    
    public RendererSet getRendererSet() {
        return rendererSet;
    }
    
    public void clear() {
        this.renderables.clear();
    }
    
    //Might use later TODO use flip() pattern?
    public boolean requiresRefill() {
        return true;
    }
    
}
