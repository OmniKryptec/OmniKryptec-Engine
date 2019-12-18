package de.omnikryptec.render.batch.module;

import org.joml.Matrix3x2fc;
import org.joml.Vector2f;

import de.omnikryptec.render.batch.module.ModuleBatchingManager.QuadSide;

public class PositionModule implements Module {
    
    @Override
    public int size() {
        return 2;
    }
    
    @Override
    public boolean sideIndependant() {
        return false;
    }
    
    private Vector2f botleft = new Vector2f();
    private Vector2f botright = new Vector2f();
    private Vector2f topleft = new Vector2f();
    private Vector2f topright = new Vector2f();
    
    public void setTransform(final float x, final float y, final float width, final float height) {
        this.botleft.set(x, y);
        this.botright.set(width + x, y);
        this.topleft.set(x, height + y);
        this.topright.set(width + x, height + y);
    }
    
    public void setTransform(final Matrix3x2fc transform, final float width, final float height) {
        this.botleft.set(0);
        this.botright.set(width, 0);
        this.topleft.set(0, height);
        this.topright.set(width, height);
        if (transform != null) {
            this.botleft = transform.transformPosition(this.botleft);
            this.botright = transform.transformPosition(this.botright);
            this.topleft = transform.transformPosition(this.topleft);
            this.topright = transform.transformPosition(this.topright);
        }
    }
    
    @Override
    public void visit(final float[] array, final QuadSide side, final int index) {
        switch (side) {
        case BotLeft:
            array[index] = this.botleft.x;
            array[index + 1] = this.botleft.y;
            break;
        case BotRight:
            array[index] = this.botright.x;
            array[index + 1] = this.botright.y;
            break;
        case TopLeft:
            array[index] = this.topleft.x;
            array[index + 1] = this.topleft.y;
            break;
        case TopRight:
            array[index] = this.topright.x;
            array[index + 1] = this.topright.y;
            break;
        default:
            throw new IllegalArgumentException();
        }
    }
    
}
