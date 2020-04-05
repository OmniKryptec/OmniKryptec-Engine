package de.omnikryptec.render.renderer;

import org.joml.Matrix4f;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.render.Camera;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.postprocessing.Postprocessor;
import de.omnikryptec.render.renderer.ViewManager.EnvironmentKey;
import de.omnikryptec.util.settings.Settings;

public class View {

    private IProjection projection;
    private FrameBuffer targetFbo;
    private Settings<EnvironmentKey> environmentSettings;
    private Postprocessor postprocessor;

    public View() {
        this.environmentSettings = new Settings<>();
        this.projection = new Camera(new Matrix4f().ortho2D(0, 1, 0, 1));
        setTargetToSurface();
    }

    public void setProjection(IProjection projection) {
        this.projection = projection;
    }

    public void setTargetToSurface() {
        setTargetFbo(LibAPIManager.instance().getGLFW().getRenderAPI().getSurface());
    }

    public void setTargetFbo(FrameBuffer targetFbo) {
        this.targetFbo = targetFbo;
    }

    public void setEnvironment(Settings<EnvironmentKey> environmentSettings) {
        this.environmentSettings = environmentSettings;
    }

    public void setPostprocessor(Postprocessor postprocessor) {
        this.postprocessor = postprocessor;
    }

    public Postprocessor getPostprocessor() {
        return this.postprocessor;
    }

    public IProjection getProjection() {
        return this.projection;
    }

    public Settings<EnvironmentKey> getEnvironment() {
        return this.environmentSettings;
    }

    public FrameBuffer getTargetFbo() {
        return this.targetFbo;
    }
}
