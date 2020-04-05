package de.omnikryptec.gui;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.render.batch.BorderedBatch2D;
import de.omnikryptec.resource.Font;
import de.omnikryptec.util.data.Color;

public class GuiProgressBar extends GuiComponentPositionable {

    private float value;
    private final Color empty = new Color();
    private final Color full = new Color();
    private final Color textColor = new Color();
    private Font font;
    private String string = "";

    private Texture emptyT, fullT;

    @Override
    protected void renderComponent(BorderedBatch2D batch, float aspect) {
        float x = getX();
        float y = getY();
        float w = getW();
        float h = getH();
        batch.color().set(this.empty);
        batch.draw(this.emptyT, x, y, w, h);
        batch.color().set(this.full);
        batch.draw(this.fullT, x, y, w * this.value, h);
        //TODO pcfreak9000 UV-animation?
        //TODO pcfreak9000 more text settings (also fix text y offset)
        if (this.font != null) {
            batch.color().set(this.textColor);
            float size = h - 9 * h / 20;
            float f = this.font.getLength(this.string, size, aspect);
            batch.drawStringSDFautoc(this.string, this.font, size, aspect, 0.57f, x - f / 2 + w / 2, y + h / 3, 0);
        }
    }

    public Color colorEmpty() {
        return this.empty;
    }

    public Color colorFull() {
        return this.full;
    }

    public Color colorText() {
        return this.textColor;
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
