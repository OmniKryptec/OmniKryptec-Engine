package de.omnikryptec.render.batch;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.TextureRegion;
import de.omnikryptec.render.batch.ModuleBatchingManager.QuadSide;

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
    
    public void set(Texture t, boolean flipU, boolean flipV) {
        if (t == null) {
            u0 = -1;
            v0 = -1;
            u1 = -1;
            v1 = -1;
        } else {
            flipV = flipV != t.requiresInvertedVifDrawn2D();
            if (t instanceof TextureRegion) {
                TextureRegion r = (TextureRegion) t;
                u0 = r.u0();
                v0 = r.v0();
                u1 = r.u1();
                v1 = r.v1();
            } else {
                u0 = 0;
                v0 = 0;
                u1 = 1;
                v1 = 1;
            }
            if (flipU) {
                u0 = 1 - u0;
                u1 = 1 - u1;
            }
            if (flipV) {
                v0 = 1 - v0;
                v1 = 1 - v1;
            }
        }
    }
    
    public void set(float u0, float v0, float u1, float v1, boolean flipV, boolean flipU) {
        if (flipU && u0 != -1) {
            u0 = 1 - u0;
            u1 = 1 - u1;
        }
        if (flipV && v0 != -1) {
            v0 = 1 - v0;
            v1 = 1 - v1;
        }
        this.u0 = u0;
        this.v0 = v0;
        this.u1 = u1;
        this.v1 = v1;
    }
    
    @Override
    public void visit(float[] array, QuadSide side, int index) {
        switch (side) {
        case BotLeft:
            array[index] = u0;
            array[index + 1] = v0;
            break;
        case BotRight:
            array[index] = u1;
            array[index + 1] = v0;
            break;
        case TopLeft:
            array[index] = u0;
            array[index + 1] = v1;
            break;
        case TopRight:
            array[index] = u1;
            array[index + 1] = v1;
            break;
        default:
            throw new IllegalArgumentException();
        }
    }
    
}
