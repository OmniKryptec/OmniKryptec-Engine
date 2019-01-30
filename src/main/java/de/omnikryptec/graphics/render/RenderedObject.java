package de.omnikryptec.graphics.render;

import org.joml.FrustumIntersection;

public interface RenderedObject {
    
    boolean isVisible(FrustumIntersection frustum);
}
