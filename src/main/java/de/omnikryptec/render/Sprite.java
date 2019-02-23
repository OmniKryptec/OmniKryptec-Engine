package de.omnikryptec.render;

import org.joml.FrustumIntersection;
import org.joml.Matrix3x2f;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.storage.RenderedObject;

public class Sprite implements RenderedObject{

    private int layer;
    private Texture texture;
    private Matrix3x2f transform;
    
    @Override
    public boolean isVisible(FrustumIntersection frustum) {
        
        return true;
    }

    public int getLayer() {
        
        return layer;
    }
    
}
