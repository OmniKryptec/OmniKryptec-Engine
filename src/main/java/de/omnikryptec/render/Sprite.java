package de.omnikryptec.render;

import org.joml.FrustumIntersection;
import org.joml.Matrix3x2f;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.storage.RenderedObject;
import de.omnikryptec.util.data.Color;

public class Sprite implements RenderedObject{

    private float layer;
    private Texture texture;
    private Matrix3x2f transform;
    private Color color;
    
    @Override
    public boolean isVisible(FrustumIntersection frustum) {
        
        return true;
    }
    
}
