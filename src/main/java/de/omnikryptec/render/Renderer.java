package de.omnikryptec.render;

import de.omnikryptec.render.storage.IRenderedObjectManager;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public interface Renderer {

    void init(Settings<?> renderSettings);
    
    default void preRender(final Time time) {

    }

    void render(Time time, IProjection projection, IRenderedObjectManager objects);

    default void postRender(final Time time) {

    }
}
