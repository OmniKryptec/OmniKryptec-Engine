package de.omnikryptec.gui;

public class GuiConstraints {
    
    private final float x;
    private final float y;
    private final float maxWidth;
    private final float maxHeight;
    
    public GuiConstraints(final float x, final float y, final float maxWidth, final float maxHeight) {
        this.x = x;
        this.y = y;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public float getMaxWidth() {
        return this.maxWidth;
    }
    
    public float getMaxHeight() {
        return this.maxHeight;
    }
    
}
