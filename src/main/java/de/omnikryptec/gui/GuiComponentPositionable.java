package de.omnikryptec.gui;

import de.omnikryptec.util.math.Mathf;

public class GuiComponentPositionable extends GuiComponent {
    
    private float mx, my, mw, mh;
    private float x, y, w, h;
    
    private boolean alwaysMax = true;
    
    @Override
    protected void calculateActualPosition(GuiConstraints constraints) {
        this.mx = constraints.getX();
        this.my = constraints.getY();
        this.mw = constraints.getMaxWidth();
        this.mh = constraints.getMaxHeight();
    }
    
    public float getX() {
        return this.alwaysMax ? this.mx : this.mx + x * mw;
    }
    
    public float getY() {
        return this.alwaysMax ? this.my : this.my + y * mh;
    }
    
    public float getW() {
        return this.alwaysMax ? this.mw : Mathf.min(mw * w, mw * (1 - x));
    }
    
    public float getH() {
        return this.alwaysMax ? this.mh : Mathf.min(mh * h, mh * (1 - y));
    }
    
    public void setDimensions(float x, float y, float w, float h) {
        setMaxAlways(false);
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }
    
    public void setMaxAlways() {
        setMaxAlways(true);
    }
    
    private void setMaxAlways(boolean b) {
        this.alwaysMax = b;
    }
    
}
