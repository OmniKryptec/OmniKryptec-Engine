package de.omnikryptec.gui;

import de.omnikryptec.util.math.Mathf;

public class GuiComponentPositionable extends GuiComponent {
    
    private float mx, my, mw, mh;
    private float x, y, w, h;
    
    private boolean alwaysMax = false;
    
    @Override
    protected void calculateActualPosition(GuiConstraints constraints) {
        this.mx = constraints.getX();
        this.my = constraints.getY();
        this.mw = constraints.getMaxWidth();
        this.mh = constraints.getMaxHeight();
    }
    
    public float getX() {
        return alwaysMax ? mx : Mathf.max(x, mx);
    }
    
    public float getY() {
        return alwaysMax ? my : Mathf.max(y, my);
    }
    
    public float getW() {
        return alwaysMax ? mw : Mathf.min(w, mw);
    }
    
    public float getH() {
        return alwaysMax ? mh : Mathf.min(h, mh);
    }
    
    public void setX(float x) {
        this.x = x;
    }
    
    public void setY(float y) {
        this.y = y;
    }
    
    public void setW(float w) {
        this.w = w;
    }
    
    public void setH(float h) {
        this.h = h;
    }
    
    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void setSize(float w, float h) {
        this.w = w;
        this.h = h;
    }
    
    public void setMaxAlways(boolean b) {
        this.alwaysMax = b;
    }
    
}
