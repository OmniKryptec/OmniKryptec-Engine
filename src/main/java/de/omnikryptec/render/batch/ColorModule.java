package de.omnikryptec.render.batch;

import de.omnikryptec.render.batch.ModuleBatchingManager.QuadSide;
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
    
    private Color color = new Color();
    
    public Color color() {
        return color;
    }
    
    @Override
    public void visit(float[] array, QuadSide side, int index) {
        array[index] = color.getR();
        array[index + 1] = color.getG();
        array[index + 2] = color.getB();
        array[index + 3] = color.getA();
    }
    
}
