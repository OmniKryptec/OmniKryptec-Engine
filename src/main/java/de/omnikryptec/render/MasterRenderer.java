package de.omnikryptec.render;

import java.util.List;

import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.render.storage.IRenderedObjectManager;
import de.omnikryptec.render.storage.RenderedObjectManager;
import de.omnikryptec.util.updater.Time;

public class MasterRenderer {

    private IRenderedObjectManager objectManager;
    private List<Renderer> renderers;

    public MasterRenderer() {
        this(new RenderedObjectManager());
    }

    public MasterRenderer(IRenderedObjectManager objectManager) {
        this.objectManager = objectManager;
    }

    public RenderAPI getRenderAPI() {
        return RenderAPI.get();
    }

    public IRenderedObjectManager getIRenderedObjectManager() {
        return objectManager;
    }

    public void addRenderer(Renderer renderer) {
        renderers.add(renderer);
        renderer.init(this);
    }

    public void removeRenderer(Renderer renderer) {
        renderer.deinit(this);
        renderers.remove(renderer);
    }

    public void preRender(Time time, IProjection projection) {
        for (Renderer r : renderers) {
            r.preRender(time, projection, this);
        }
    }

    public void render(Time time, IProjection projection) {
        for (Renderer r : renderers) {
            r.render(time, projection, this);
        }
    }

    public void postRender(Time time, IProjection projection) {
        for (Renderer r : renderers) {
            r.postRender(time, projection, this);
        }
    }
}
