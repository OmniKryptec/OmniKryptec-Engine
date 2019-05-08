package de.omnikryptec.render.objects;

import org.joml.Matrix3x2f;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.util.data.Color;

public class SimpleSprite extends Sprite {
    //TODO transform
    private Matrix3x2f transform = new Matrix3x2f();
    private float width = 1, height = 1;
    
    private Color color;
    private Texture texture;
    
    @Override
    public void draw(Batch2D batch) {
        batch.color().set(color == null ? Color.ONE : color);
        batch.draw(texture, transform, width, height, false, false);
    }
    
    public Matrix3x2f getTransform() {
        return transform;
    }
    
    public void setTransform(Matrix3x2f mat) {
        this.transform = mat;
    }
    
    public float getWidth() {
        return width;
    }
    
    public void setWidth(float width) {
        this.width = width;
    }
    
    public float getHeight() {
        return height;
    }
    
    public void setHeight(float height) {
        this.height = height;
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public void setTexture(Texture tex) {
        this.texture = tex;
    }
    
    public Texture getTexture() {
        return texture;
    }
    
}
