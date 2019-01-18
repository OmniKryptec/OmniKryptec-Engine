package de.omnikryptec.graphics.render;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import de.omnikryptec.util.Util;

public class Viewport {

    private Map<Renderer, List<?>> renderables;

    public Viewport() {
        renderables = new HashMap<>();
    }

    private void add(RenderManager mgr, Renderer renderer, Object obj) {
        if (!mgr.supports(renderer)) {
            throw new IllegalArgumentException("renderer not supported");
        }
        List<?> list = renderables.get(renderer);
        if (list == null) {
            list = Util.ensureNonNull(renderer.createRenderList());
            renderables.put(renderer, list);
        }
        list.add(obj);
    }

}
