package de.omnikryptec.render.batch.module;

import de.omnikryptec.render.batch.module.ModuleBatchingManager.QuadSide;
import de.omnikryptec.util.data.Color;

public class ColorModule implements Module {

    @Override
    public int size() {
        return 4;
    }

    @Override
    public boolean sideIndependant() {
        return true;
    }

    private final Color color = new Color();

    public Color color() {
        return this.color;
    }

    @Override
    public void visit(final float[] array, final QuadSide side, final int index) {
        array[index] = this.color.getR();
        array[index + 1] = this.color.getG();
        array[index + 2] = this.color.getB();
        array[index + 3] = this.color.getA();
    }

}
