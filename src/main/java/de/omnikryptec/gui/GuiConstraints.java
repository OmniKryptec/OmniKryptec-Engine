package de.omnikryptec.gui;

public class GuiConstraints {
    
    private float x;
    private float y;
    private float maxWidth;
    private float maxHeight;
    
    public GuiConstraints(float x, float y, float maxWidth, float maxHeight) {
        this.x = x;
        this.y = y;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getMaxWidth() {
        return maxWidth;
    }
    
    public float getMaxHeight() {
        return maxHeight;
    }
    
}
