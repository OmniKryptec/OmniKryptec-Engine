package de.omnikryptec.render;

import org.joml.FrustumIntersection;

import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.render.storage.RenderedObject;
import de.omnikryptec.render.storage.RenderedObjectType;

public abstract class Sprite implements RenderedObject {
    public static final RenderedObjectType TYPE = RenderedObjectType.of(Sprite.class);
    
    private float layer;
    
    @Override
    public boolean isVisible(FrustumIntersection frustum) {
        return true;
    }
    
    public float getLayer() {
        return layer;
    }
    
    public void setLayer(float layer) {
        this.layer = layer;
    }
    
    public abstract void draw(Batch2D batch);
    
}
