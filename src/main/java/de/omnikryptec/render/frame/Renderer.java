package de.omnikryptec.render.frame;

import java.util.Collection;

import de.omnikryptec.render.IProjection;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public interface Renderer {

    void init(Settings<?> renderSettings);
    
    default void preRender(final Time time) {

    }

    void render(Time time, IProjection projection, Collection<RenderedObject> objs);

    default void postRender(final Time time) {

    }

    Collection<RenderedObject> createRenderList();

    default boolean supportsObjects() {
        return true;
    }
}
