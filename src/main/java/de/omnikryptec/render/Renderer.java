package de.omnikryptec.render;

import de.omnikryptec.util.updater.Time;

public interface Renderer {

    void init(MasterRenderer renderer);

    default void preRender(final Time time, IProjection projection, MasterRenderer renderer) {

    }

    void render(Time time, IProjection projection, MasterRenderer renderer);

    default void postRender(final Time time, IProjection projection, MasterRenderer renderer) {

    }

    void deinit(MasterRenderer renderer);
}
