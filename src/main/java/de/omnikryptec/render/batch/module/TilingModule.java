package de.omnikryptec.render.batch.module;

import de.omnikryptec.render.batch.module.ModuleBatchingManager.QuadSide;

public class TilingModule implements Module {

    private float factor = 1;

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean sideIndependant() {
        return true;
    }

    @Override
    public void visit(float[] array, QuadSide side, int index) {
        array[index] = this.factor;
    }

    public void setTilingFactor(float f) {
        this.factor = f;
    }

    public float getTilingFactor() {
        return this.factor;
    }

}
