package de.omnikryptec.gui;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.BorderedBatch2D;
import de.omnikryptec.util.data.Color;

public class GuiImage extends GuiComponentPositionable {
    
    private Texture texture;
    private Color color = new Color();
    
    @Override
    protected void renderComponent(BorderedBatch2D batch, float aspect) {
        batch.color().set(color);
        batch.draw(texture, getX(), getY(), getW(), getH());
    }
    
    public void setTexture(Texture t) {
        this.texture = t;
    }
    
    public Color color() {
        return color;
    }
    
}
