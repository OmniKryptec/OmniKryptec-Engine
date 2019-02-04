package de.omnikryptec.render;

import org.joml.FrustumIntersection;

public interface RenderedObject {

    boolean isVisible(FrustumIntersection frustum);
}
