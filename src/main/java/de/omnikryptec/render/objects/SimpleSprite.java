package de.omnikryptec.render.objects;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.transform.Transform2Df;

public class SimpleSprite extends Sprite {

    private Transform2Df transform = new Transform2Df();
    private float width = 1;
    private float height = 1;

    private Color color;
    private Texture texture;

    @Override
    public void draw(final Batch2D batch) {
        batch.color().set(this.color == null ? Color.ONE : this.color);
        batch.draw(this.texture, this.transform.worldspace(), this.width, this.height, false, false);
    }

    public Transform2Df getTransform() {
        return this.transform;
    }

    public void setTransform(final Transform2Df mat) {
        this.transform = mat;
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(final float width) {
        this.width = width;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(final float height) {
        this.height = height;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(final Color color) {
        this.color = color;
    }

    public void setTexture(final Texture tex) {
        this.texture = tex;
    }

    public Texture getTexture() {
        return this.texture;
    }

}
