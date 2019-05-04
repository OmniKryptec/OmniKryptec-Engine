package de.omnikryptec.render.objects;

import org.joml.Matrix3x2f;
import org.joml.Vector2f;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.Batch2D;
import de.omnikryptec.util.data.Color;

public class SimpleSprite extends Sprite {
    //TODO transform
    private Vector2f position;
    private float width = 1, height = 1;
    
    private Color color;
    private Texture texture;
    
    @Override
    public void draw(Batch2D batch) {
        batch.color().set(color == null ? Color.ONE : color);
        batch.draw(texture, new Matrix3x2f().setTranslation(position), width, height, false, false);
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
    
    public void setTexture(Texture tex) {
        this.texture = tex;
    }
    
    public Texture getTexture() {
        return texture;
    }
    
}
