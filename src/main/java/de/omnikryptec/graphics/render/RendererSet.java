package de.omnikryptec.graphics.render;

import java.util.HashSet;
import java.util.Set;

import de.omnikryptec.util.Util;

public class RendererSet {
    
    private Set<Renderer> renderers;
    
    public RendererSet() {
        renderers = new HashSet<>();
    }
    
    public void addRenderer(Renderer renderer) {
        Util.ensureNonNull(renderer);
        renderers.add(renderer);
    }
    
    public boolean supports(Renderer renderer) {
        return renderers.contains(renderer);
    }

}
