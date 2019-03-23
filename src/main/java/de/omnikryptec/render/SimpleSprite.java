package de.omnikryptec.render;

import org.joml.Matrix3x2f;
import org.joml.Vector2f;

import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.util.data.Color;

public class SimpleSprite extends Sprite {
    
    private Vector2f position;
    private float width, height;
    
    private Color color;
    
    @Override
    public void draw(Batch2D batch) {
        if (color != null) {
            batch.color().set(color);
        }
        batch.drawRect(new Matrix3x2f().setTranslation(position), width, height);
    }
    
    public Vector2f getPosition() {
        return position;
    }
    
    public void setPosition(Vector2f position) {
        this.position = position;
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

   
    
}
