package de.omnikryptec.render.renderer;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.window.SurfaceBuffer;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.util.updater.Time;

public interface Renderer {

    void init(LocalRendererContext context, FrameBuffer target);

    default void preRender(final Time time, final IProjection projection, final LocalRendererContext context) {
    }

    void render(Time time, IProjection projection, LocalRendererContext context);

    default void postRender(final Time time, final IProjection projection, final LocalRendererContext context) {
    }

    default void resizeFBOs(final LocalRendererContext context, final SurfaceBuffer screen) {
    }

    void deinit(LocalRendererContext context);

    default int priority() {
        return 0;
    }
}
