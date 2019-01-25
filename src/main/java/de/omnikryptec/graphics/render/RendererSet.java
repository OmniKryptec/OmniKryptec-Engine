package de.omnikryptec.graphics.render;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.omnikryptec.util.Util;
import de.omnikryptec.util.settings.Settings;

public class RendererSet {

    private Set<Renderer> renderers;
    private List<Renderer> renderersList;

    public RendererSet() {
        renderers = new HashSet<>();
        renderersList = new ArrayList<>();
    }

    public void addRenderer(Renderer renderer) {
        Util.ensureNonNull(renderer);
        if (renderers.add(renderer)) {
            renderersList.add(renderer);
        }
    }

    public boolean supports(Renderer renderer) {
        return renderers.contains(renderer);
    }

    public void prepareRenderers(Settings<?> renderSettings) {
        for (Renderer rend : renderersList) {
            rend.preRender(renderSettings);
        }
    }

    public void finishRenderers(Settings<?> renderSettings) {
        for (Renderer rend : renderersList) {
            rend.postRender(renderSettings);
        }
    }

}
