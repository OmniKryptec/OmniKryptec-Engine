package de.omnikryptec.render.objects;

import org.joml.FrustumIntersection;

public interface RenderedObject {

    boolean isVisible(FrustumIntersection frustum);

    //TODO is renderedobjecttype even a good thing? performance?
    RenderedObjectType type();
}
