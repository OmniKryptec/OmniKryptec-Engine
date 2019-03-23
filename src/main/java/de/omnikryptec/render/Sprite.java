package de.omnikryptec.render;

import org.joml.FrustumIntersection;
import org.joml.Matrix3x2f;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.render.storage.RenderedObject;
import de.omnikryptec.render.storage.RenderedObjectType;
import de.omnikryptec.util.data.Color;

public class Sprite implements RenderedObject {
    public static final RenderedObjectType TYPE = RenderedObjectType.of(Sprite.class);
    
    private float layer;
    private float reflection;
    private Texture texture;
    private Matrix3x2f transform = new Matrix3x2f();
    private Color color;
    
    @Override
    public boolean isVisible(FrustumIntersection frustum) {
        return true;
    }
    
    public float getLayer() {
        return layer;
    }
    
    public Matrix3x2f getTransform() {
        return transform;
    }
    
    public void draw(Batch2D batch) {
        if (color != null) {
            batch.color().set(color);
        }
        batch.draw(texture, transform, false, false);
    }
    
}
