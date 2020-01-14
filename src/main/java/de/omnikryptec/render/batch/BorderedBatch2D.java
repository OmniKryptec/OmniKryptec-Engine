package de.omnikryptec.render.batch;

import org.joml.Vector2f;

import de.omnikryptec.util.data.Color;

public interface BorderedBatch2D extends Batch2D {
    Color borderColor();
    
    Vector2f borderSDFData();
    
    Vector2f borderOffset();
    
    Vector2f signedDistanceFieldData();
    
    void setDefaultSdfData();
}
