package de.omnikryptec.render3.d2.sprites;

import org.joml.FrustumIntersection;

public abstract class AbstractSprite {
    
    private int layer;
    
    public boolean isVisible(final FrustumIntersection frustum) {
        return true;
    }
    
    //Meh... what if it changes but is already added...?
    public int getLayer() {
        return this.layer;
    }
    
    @Deprecated //For now... maybe make layer final or smth
    public void setLayer(int layer) { 
        this.layer = layer;
    }
    
    public abstract IRenderer2D getRenderer();
    
    public abstract void draw();
}
