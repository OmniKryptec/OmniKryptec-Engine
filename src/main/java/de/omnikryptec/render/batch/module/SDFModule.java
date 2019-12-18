package de.omnikryptec.render.batch.module;

import org.joml.Vector2f;

import de.omnikryptec.render.batch.module.ModuleBatchingManager.QuadSide;

public class SDFModule implements Module {

    @Override
    public int size() {
        return 4;
    }

    @Override
    public boolean sideIndependant() {
        return true;
    }

    private final Vector2f sdData = new Vector2f(0, 1);
    private final Vector2f bsdData = new Vector2f(0, 1);

    @Override
    public void visit(float[] array, QuadSide side, int index) {
        array[index] = this.sdData.x();
        array[index + 1] = this.sdData.y();
        array[index + 2] = this.bsdData.x();
        array[index + 3] = this.bsdData.y();
    }

    public Vector2f sdfData() {
        return this.sdData;
    }

    public Vector2f bsdfData() {
        return this.bsdData;
    }

    public void setDefault() {
        this.sdData.set(0, 1);
        this.bsdData.set(0, 1);
    }

}
