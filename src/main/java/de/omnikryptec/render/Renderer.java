package de.omnikryptec.render;

import java.util.Collection;

import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public interface Renderer {

    default void preRender(final Time time, final Settings<?> renderSettings) {

    }

    void render(Time time, IProjection projection, Collection<RenderedObject> objs, Settings<?> renderSettings);

    default void postRender(final Time time, final Settings<?> renderSettings) {

    }

    Collection<RenderedObject> createRenderList();

    default boolean supportsObjects() {
        return true;
    }
}
