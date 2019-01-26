package de.omnikryptec.graphics.render;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.omnikryptec.util.Util;
import de.omnikryptec.util.settings.Settings;

public class RendererSet {
    
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
    
    public void prepareRenderers(final Settings<?> renderSettings) {
        for (final Renderer rend : this.renderersList) {
            rend.preRender(renderSettings);
        }
    }
    
    public void finishRenderers(final Settings<?> renderSettings) {
        for (final Renderer rend : this.renderersList) {
            rend.postRender(renderSettings);
        }
    }
    
}
