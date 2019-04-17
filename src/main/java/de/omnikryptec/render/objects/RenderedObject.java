package de.omnikryptec.render.objects;

import org.joml.FrustumIntersection;

public interface RenderedObject {
    
    boolean isVisible(FrustumIntersection frustum);
}
