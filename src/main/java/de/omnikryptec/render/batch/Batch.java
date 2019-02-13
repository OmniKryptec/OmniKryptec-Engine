package de.omnikryptec.render.batch;

import org.joml.Matrix4fc;

import de.omnikryptec.util.data.Color;

public interface Batch {
    
    void begin();
    
    void setGlobalTransform(Matrix4fc mat);
    
    void setColor(Color color);
    
    void flush();
    
    void end();
}
