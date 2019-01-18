package de.omnikryptec.graphics.render;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.omnikryptec.util.Util;

public class RenderManager {

    private Set<Renderer> renderers;

    public RenderManager() {
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
