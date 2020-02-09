package de.omnikryptec.render.renderer2;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.renderer.RendererContext.EnvironmentKey;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public interface Renderer {
    
    default int priority() {
        return 0;
    }
    
    default void prepare(InstanceManager instanceManager, RenderAPI api, IProjection mainProjection, Time time) {
    }
    
    void add(Renderable renderable);
    
    void remove(Renderable renderable);
    
    void render(InstanceManager instanceManager, RenderAPI api, IProjection projection, FrameBuffer target,
            Settings<EnvironmentKey> envSettings, Time time);
}
