package de.omnikryptec.render.objects;

import org.joml.FrustumIntersection;

import de.omnikryptec.render.batch.Batch2D;

public abstract class Sprite implements RenderedObject {
    public static final RenderedObjectType TYPE = RenderedObjectType.of(Sprite.class);

    private float layer;

    @Override
    public boolean isVisible(final FrustumIntersection frustum) {
        return true;
    }

    public float getLayer() {
        return this.layer;
    }

    public void setLayer(final float layer) {
        this.layer = layer;
    }

    public abstract void draw(Batch2D batch);

    @Override
    public RenderedObjectType type() {
        return TYPE;
    }
}
