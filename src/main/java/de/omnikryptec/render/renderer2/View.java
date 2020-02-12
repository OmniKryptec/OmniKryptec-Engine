package de.omnikryptec.render.renderer2;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.postprocessing.Postprocessor;
import de.omnikryptec.render.renderer2.ViewManager.EnvironmentKey;
import de.omnikryptec.util.settings.Settings;

public class View {
    
    private IProjection projection;
    private FrameBuffer targetFbo;
    private Settings<EnvironmentKey> environmentSettings;
    private Postprocessor postprocessor;
    
    public View() {
        this.environmentSettings = new Settings<>();
    }
    
    public void setProjection(IProjection projection) {
        this.projection = projection;
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
        return postprocessor;
    }
    
    public IProjection getProjection() {
        return projection;
    }
    
    public Settings<EnvironmentKey> getEnvironment() {
        return environmentSettings;
    }
    
    public FrameBuffer getTargetFbo() {
        return targetFbo;
    }
}
