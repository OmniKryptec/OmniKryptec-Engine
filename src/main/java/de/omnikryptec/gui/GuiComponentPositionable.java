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
        return this.alwaysMax ? this.mx : Mathf.max(this.x, this.mx);
    }

    public float getY() {
        return this.alwaysMax ? this.my : Mathf.max(this.y, this.my);
    }

    public float getW() {
        return this.alwaysMax ? this.mw : Mathf.min(this.w, this.mw);
    }

    public float getH() {
        return this.alwaysMax ? this.mh : Mathf.min(this.h, this.mh);
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
