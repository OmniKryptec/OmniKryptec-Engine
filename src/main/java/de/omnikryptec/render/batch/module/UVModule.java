package de.omnikryptec.render.batch.module;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.TextureRegion;
import de.omnikryptec.render.batch.module.ModuleBatchingManager.QuadSide;

public class UVModule implements Module {

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean sideIndependant() {
        return false;
    }

    private float u0, v0, u1, v1;

    public void set(final Texture t, final boolean flipU, boolean flipV) {
        if (t == null) {
            return;
        }
        flipV = flipV != t.requiresInvertedVifDrawn2D();
        if (t instanceof TextureRegion) {
            final TextureRegion r = (TextureRegion) t;
            this.u0 = r.u0();
            this.v0 = r.v0();
            this.u1 = r.u1();
            this.v1 = r.v1();
        } else {
            this.u0 = 0;
            this.v0 = 0;
            this.u1 = 1;
            this.v1 = 1;
        }
        if (flipU) {
            float tmp = this.u0;
            this.u0 = this.u1;
            this.u1 = tmp;
        }
        if (flipV) {
            float tmp = this.v0;
            this.v0 = this.v1;
            this.v1 = tmp;
        }
    }

    public void set(float u0, float v0, float u1, float v1, final boolean flipV, final boolean flipU) {
        if (flipU) {
            u0 = 1 - u0;
            u1 = 1 - u1;
        }
        if (flipV) {
            v0 = 1 - v0;
            v1 = 1 - v1;
        }
        this.u0 = u0;
        this.v0 = v0;
        this.u1 = u1;
        this.v1 = v1;
    }

    @Override
    public void visit(final float[] array, final QuadSide side, final int index) {
        switch (side) {
        case BotLeft:
            array[index] = this.u0;
            array[index + 1] = this.v0;
            break;
        case BotRight:
            array[index] = this.u1;
            array[index + 1] = this.v0;
            break;
        case TopLeft:
            array[index] = this.u0;
            array[index + 1] = this.v1;
            break;
        case TopRight:
            array[index] = this.u1;
            array[index + 1] = this.v1;
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

}
