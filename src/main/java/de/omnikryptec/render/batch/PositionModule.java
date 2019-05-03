package de.omnikryptec.render.batch;

import org.joml.Matrix3x2fc;
import org.joml.Vector2f;

import de.omnikryptec.render.batch.ModuleBatchingManager.QuadSide;

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
    
    public void setTransform(float x, float y, float width, float height) {
        botleft.set(x, y);
        botright.set(width + x, y);
        topleft.set(x, height + y);
        topright.set(width + x, height + y);
    }
    
    public void setTransform(Matrix3x2fc transform, float width, float height) {
        botleft.set(0);
        botright.set(width, 0);
        topleft.set(0, height);
        topright.set(width, height);
        if (transform != null) {
            botleft = transform.transformPosition(botleft);
            botright = transform.transformPosition(botright);
            topleft = transform.transformPosition(topleft);
            topright = transform.transformPosition(topright);
        }
    }
    
    @Override
    public void visit(float[] array, QuadSide side, int index) {
        switch (side) {
        case BotLeft:
            array[index] = botleft.x;
            array[index + 1] = botleft.y;
            break;
        case BotRight:
            array[index] = botright.x;
            array[index + 1] = botright.y;
            break;
        case TopLeft:
            array[index] = topleft.x;
            array[index + 1] = topleft.y;
            break;
        case TopRight:
            array[index] = topright.x;
            array[index + 1] = topright.y;
            break;
        default:
            throw new IllegalArgumentException();
        }
    }
    
}
