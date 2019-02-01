package de.omnikryptec.graphics.render;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderUtil;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.math.MathUtil;
import de.omnikryptec.util.math.Struct3f;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class Viewport {

    private final RendererSet rendererSet;
    private final Map<Renderer, DisplayList> renderables;

    private IProjection projection;
    private Struct3f position;

    private boolean visibilityOverride = false;
    private boolean refill = true;

    public Viewport(final RendererSet rendererSet) {
        this.renderables = new HashMap<>();
        this.rendererSet = Util.ensureNonNull(rendererSet);
    }

    public void render(final Time time, final FrameBuffer target, final Settings<?> renderSettings) {
        if (this.projection == null) {
            System.err.println("No projection set");
            return;
        }
        RenderUtil.bindIfNonNull(target);
        for (final Renderer renderer : this.renderables.keySet()) {
            renderer.render(time, this.projection, renderer.supportsObjects() ? this.renderables.get(renderer) : null,
                    renderSettings);
        }
        RenderUtil.unbindIfNonNull(target);
    }

    public void add(final Renderer renderer, final Collection<? extends RenderedObject> objs) {
        Util.ensureNonNull(objs);
        if (objs.isEmpty()) {
            return;
        }
        final DisplayList list = checkAndGetDisplayList(renderer);
        for (final RenderedObject r : objs) {
            addUnchecked(list, r);
        }
    }

    public void add(final Renderer renderer, final RenderedObject robj) {
        Util.ensureNonNull(robj);
        addUnchecked(checkAndGetDisplayList(renderer), robj);
    }

    private void addUnchecked(final DisplayList list, final RenderedObject robj) {
        if (this.visibilityOverride || robj.isVisible(this.projection.getFrustumTester())) {
            list.addObject(robj);
        }
    }

    private DisplayList checkAndGetDisplayList(final Renderer renderer) {
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

    public void reset() {
        this.reset(this.projection, this.position);
    }

    public void reset(final IProjection projection, final Struct3f position) {
        boolean changed = false;
        if (projection != null) {
            this.projection = projection;
            changed = true;
        }
        if (position != null && !MathUtil.equals(position, this.position)) {
            this.position = position;
            changed = true;
        }
        if (changed) {
            this.refill = true;
            this.renderables.clear();
        }
    }

    //Change to autoflip and clear if refill==true on adding new renderedobjs?
    public void flip() {
        this.refill = false;
    }

    public void setVisibilityOverride(final boolean b) {
        this.visibilityOverride = b;
    }

    //TODO moving objects?
    public boolean requiresRefill() {
        return this.refill;
    }

    public IProjection getProjection() {
        return this.projection;
    }

    public Struct3f getPosition() {
        return this.position;
    }

    public RendererSet getRendererSet() {
        return this.rendererSet;
    }

}
