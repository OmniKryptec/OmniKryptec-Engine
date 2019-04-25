package de.omnikryptec.render.batch;

import de.omnikryptec.render.batch.ModuleBatchingManager.QuadSide;

public interface Module {
    
    int size();
    
    boolean sideIndependant();
    
    void visit(float[] array, QuadSide side, int index);
}
