package de.omnikryptec.render.objects;

import org.joml.FrustumIntersection;

public interface RenderedObject {
    
    boolean isVisible(FrustumIntersection frustum);
    
    //TODO pcfreak9000 is renderedobjecttype even a good thing? performance?
    RenderedObjectType type();
}
