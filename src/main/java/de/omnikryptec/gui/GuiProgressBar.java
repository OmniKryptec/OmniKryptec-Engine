package de.omnikryptec.gui;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.BorderedBatch2D;
import de.omnikryptec.resource.Font;
import de.omnikryptec.util.data.Color;

public class GuiProgressBar extends GuiComponentPositionable {
    
    private float value;
    private Color empty = new Color();
    private Color full = new Color();
    private Color textColor = new Color();
    private Font font;
    private String string = "";
    
    private Texture emptyT, fullT;
    
    @Override
    protected void renderComponent(BorderedBatch2D batch, float aspect) {
        float x = getX();
        float y = getY();
        float w = getW();
        float h = getH();
        batch.color().set(empty);
        batch.draw(emptyT, x, y, w, h);
        batch.color().set(full);
        batch.draw(fullT, x, y, w * value, h);
        //TODO pcfreak9000 UV-animation?
        //TODO pcfreak9000 more text settings (also fix text y offset)
        if (font != null) {
            batch.color().set(textColor);
            float size = h - 9 * h / 20;
            float f = font.getLength(string, size, aspect);
            batch.drawStringSDFautoc(string, font, size, aspect, 0.57f, x - f / 2 + w / 2, y + h / 3, 0);
        }
    }
    
    public Color colorEmpty() {
        return empty;
    }
    
    public Color colorFull() {
        return full;
    }
    
    public Color colorText() {
        return textColor;
    }
    
    public void setTextureEmpty(Texture t) {
        this.emptyT = t;
    }
    
    public void setTextureFull(Texture t) {
        this.fullT = t;
    }
    
    public void setValue(float f) {
        this.value = f;
    }
    
    public float getValue() {
        return this.value;
    }
    
    public void setFont(Font f) {
        this.font = f;
    }
    
    public void setText(String s) {
        this.string = s;
    }
}
