package de.omnikryptec.render.frame;

import org.joml.FrustumIntersection;

public interface RenderedObject {
    
    boolean isVisible(FrustumIntersection frustum);
}
