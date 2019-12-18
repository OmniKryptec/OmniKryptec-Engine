package de.omnikryptec.render.batch.module;

import de.omnikryptec.render.batch.module.ModuleBatchingManager.QuadSide;

public interface Module {
    
    int size();
    
    boolean sideIndependant();
    
    void visit(float[] array, QuadSide side, int index);
}
