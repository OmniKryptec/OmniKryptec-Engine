package de.omnikryptec.render3.sprites;

import org.joml.FrustumIntersection;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.renderer.ViewManager;
import de.omnikryptec.render3.Batch2D;
import de.omnikryptec.util.updater.Time;

public abstract class AbstractSprite {
    
    private int layer;
    
    public boolean isVisible(final FrustumIntersection frustum) {
        return true;
    }
    
    //Meh... what if it changes but is already added...?
    public int getLayer() {
        return this.layer;
    }
    
    public abstract IRenderer2D getRenderer();
    
    public abstract void draw();
}
