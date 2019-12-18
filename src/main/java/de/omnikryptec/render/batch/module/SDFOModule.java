package de.omnikryptec.render.batch.module;

import org.joml.Vector2f;

import de.omnikryptec.render.batch.module.ModuleBatchingManager.QuadSide;

public class SDFOModule implements Module {

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean sideIndependant() {
        return true;
    }

    private final Vector2f offset = new Vector2f(0, 0);

    @Override
    public void visit(float[] array, QuadSide side, int index) {
        array[index] = this.offset.x();
        array[index + 1] = this.offset.y();
    }

    public Vector2f bsdfOffset() {
        return this.offset;
    }

    public void setDefault() {
        this.offset.set(0);
    }

}
