package de.omnikryptec.render3.sprites;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.renderer.Renderer;
import de.omnikryptec.render.renderer.ViewManager;
import de.omnikryptec.render.renderer.ViewManager.EnvironmentKey;
import de.omnikryptec.render3.Batch2D;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class Renderer2D implements Renderer {
    
    private Batch2D batch;
    
    @Override
    public void render(ViewManager viewManager, RenderAPI api, IProjection projection, FrameBuffer target,
            Settings<EnvironmentKey> envSettings, Time time) {
    }
    
    //be careful...
    public Batch2D getBatch() {
        return batch;
    }
}
