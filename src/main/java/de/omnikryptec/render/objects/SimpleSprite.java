package de.omnikryptec.render.objects;

import org.joml.Matrix3x2f;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.transform.Transform2Df;

public class SimpleSprite extends Sprite {
    //TODO transform
    private Transform2Df transform = new Transform2Df();
    private float width = 1, height = 1;
    
    private Color color;
    private Texture texture;
    
    @Override
    public void draw(Batch2D batch) {
        batch.color().set(color == null ? Color.ONE : color);
        batch.draw(texture, transform.worldspace(), width, height, false, false);
    }
    
    public Transform2Df getTransform() {
        return transform;
    }
    
    public void setTransform(Transform2Df mat) {
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
