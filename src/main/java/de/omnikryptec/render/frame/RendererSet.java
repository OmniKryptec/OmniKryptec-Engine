package de.omnikryptec.render.frame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.omnikryptec.render.Renderer;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;
@Deprecated
public class RendererSet {
    
    public static final RendererSet RENDERERS_3D;
    public static final RendererSet RENDERERS_2D;
    
    static {
        RENDERERS_3D = new RendererSet();
        RENDERERS_2D = new RendererSet();
    }
    
    private final Set<Renderer> renderers;
    private final List<Renderer> renderersList;
    
    public RendererSet() {
        this.renderers = new HashSet<>();
        this.renderersList = new ArrayList<>();
    }
    
    public void addRenderer(final Renderer renderer) {
        Util.ensureNonNull(renderer);
        if (this.renderers.add(renderer)) {
            this.renderersList.add(renderer);
        }
    }
    
    public boolean supports(final Renderer renderer) {
        return this.renderers.contains(renderer);
    }
    
    public void prepareRenderers(final Time time) {
        for (final Renderer rend : this.renderersList) {
            rend.preRender(time);
        }
    }
    
    public void finishRenderers(final Time time) {
        for (final Renderer rend : this.renderersList) {
            rend.postRender(time);
        }
    }
    
    public void initRenderers(Settings<?> renderSettings) {
        for (Renderer rend : this.renderersList) {
            rend.init(renderSettings);
        }
    }
    
    public List<Renderer> getSupportedRenderer() {
        return Collections.unmodifiableList(this.renderersList);
    }
    
}
