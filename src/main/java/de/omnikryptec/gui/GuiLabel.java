package de.omnikryptec.gui;

import de.omnikryptec.render.batch.BorderedBatch2D;
import de.omnikryptec.resource.Font;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.data.Color;

public class GuiLabel extends GuiComponentPositionable {
    
    private Color color;
    private Font font;
    private String text;
    private float thickness;
    private float size;
    
    public GuiLabel() {
        this.color = new Color();
        this.text = "";
        this.thickness = 0.57f;
        this.size = 0.02f;
    }
    
    @Override
    protected void renderComponent(BorderedBatch2D batch, float aspect) {
        batch.color().set(color);
        batch.drawStringSDFautoc(text, font, size, aspect, thickness, getX(), getY(), 0);
    }
    
    public Color color() {
        return color;
    }
    
    public Font getFont() {
        return font;
    }
    
    public void setFont(Font font) {
        this.font = Util.ensureNonNull(font);
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = Util.ensureNonNull(text);
    }
    
    public float getThickness() {
        return thickness;
    }
    
    public void setThickness(float thickness) {
        this.thickness = thickness;
    }
    
    public float getSize() {
        return size;
    }
    
    public void setSize(float size) {
        this.size = size;
    }
    
}
