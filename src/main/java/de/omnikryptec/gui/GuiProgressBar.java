package de.omnikryptec.gui;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.BorderedBatch2D;
import de.omnikryptec.util.data.Color;

public class GuiProgressBar extends GuiComponent {
    
    private float value;
    private Color empty = new Color();
    private Color full = new Color();
    private Texture emptyT, fullT;
    
    private float x, y, w, h;
    
    @Override
    protected void renderComponent(BorderedBatch2D batch, float aspect) {
        batch.color().set(empty);
        batch.draw(emptyT, x, y, w, h);
        batch.color().set(full);
        batch.draw(fullT, x + w / 20, y + h / 20, (w - 2 * w / 20) * value, h - 2 * h / 20);
        //TODO pcfreak9000 UV-animation?
    }
    
    public void setColorEmpty(Color c) {
        this.empty = c;
    }
    
    public void setColorFull(Color c) {
        this.full = c;
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
    
    @Override
    protected void calculateActualPosition(GuiConstraints constraints) {
        this.x = constraints.getX();
        this.y = constraints.getY();
        this.w = constraints.getMaxWidth();
        this.h = constraints.getMaxHeight();
    }
}
