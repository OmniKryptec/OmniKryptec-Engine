package de.omnikryptec.render.renderer;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.renderer.ViewManager.EnvironmentKey;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public interface Renderer {

    default int priority() {
        return 0;
    }

    default void init(ViewManager viewManager, RenderAPI api) {
    }

    default void deinit(ViewManager viewManager, RenderAPI api) {
    }

    void render(ViewManager viewManager, RenderAPI api, IProjection projection, FrameBuffer target,
            Settings<EnvironmentKey> envSettings, Time time);
}
