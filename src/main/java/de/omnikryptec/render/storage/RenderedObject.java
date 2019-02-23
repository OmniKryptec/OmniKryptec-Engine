package de.omnikryptec.render.storage;

import org.joml.FrustumIntersection;

public interface RenderedObject {
    
    boolean isVisible(FrustumIntersection frustum);
}
